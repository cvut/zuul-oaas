/*
 * The MIT License
 *
 * Copyright 2013-2015 Czech Technical University in Prague.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package cz.cvut.zuul.oaas.support

import com.google.common.io.Resources
import com.mongodb.DBObject
import com.mongodb.util.JSON
import groovy.util.logging.Slf4j
import org.springframework.core.io.Resource
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.util.Assert

import java.nio.charset.Charset

@Slf4j
class MongoSeedLoader {

    private static final ENCODING = Charset.forName('UTF-8')

    private final URL location
    private final MongoOperations mongo

    /**
     * Drop all collections before seeding? Default is <tt>false</tt>.
     */
    boolean dropAll = false

    /**
     * Skip import for collections that already exists? Default is <tt>true</tt>.
     */
    boolean skipNonEmptyCollections = true


    /**
     * The seed file must follow this structure:
     * <pre>
     *   {
     *     'first_collection_name': [
     *       { entry_1 },
     *       { entry_2 },
     *       ...
     *     ],
     *     'second_collection_name': [
     *       ...
     *     ]
     *   }
     * </pre>
     *
     * @param location location of the JSON file with seed data.
     * @param mongo
     */
    MongoSeedLoader(Resource location, MongoOperations mongo) {
        this.location = location.getURL()
        this.mongo = mongo
    }


    void seed()  {
        log.info 'Loading seed data into database...'

        if (dropAll) dropAll()

        def parsed = JSON.parse(Resources.toString(location, ENCODING))

        Assert.state(parsed instanceof DBObject,
                'Parsed object must be instance of DBObject')
        def dbObject = (DBObject) parsed

        dbObject.keySet().each { collectionName ->
            def collection = mongo.getCollection(collectionName)

            if (skipNonEmptyCollections && collection.count() != 0) {
                log.info 'Collection {} is not empty, skipping seed', collectionName

            } else {
                log.info 'Seeding collection: {}', collectionName

                def entries = dbObject.get(collectionName)

                Assert.state(entries instanceof List,
                        'There must be list of entries under the collection key')
                collection.insert((List) entries)
            }
        }
    }

    protected void dropAll() {
        mongo.collectionNames.findAll { !it.startsWith('system') }.each { name ->
            log.warn 'Dropping collection: {}', name
            mongo.dropCollection(name)
        }
    }
}
