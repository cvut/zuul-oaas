/*
 * The MIT License
 *
 * Copyright 2013-2014 Czech Technical University in Prague.
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
package cz.cvut.zuul.oaas.repos.mongo.support;

import com.google.common.io.Resources;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.util.Assert;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

@Slf4j
public class MongoSeedLoader {

    private static final Charset ENCODING = Charset.forName("UTF-8");

    private MongoOperations mongo;
    private URL location;

    /**
     * Drop all collections before seeding? Default is <tt>false</tt>.
     */
    private @Setter boolean dropAll = false;

    /**
     * Skip import for collections that already exists? Default is <tt>true</tt>.
     */
    private @Setter boolean skipNonEmptyCollections = true;



    @SuppressWarnings("unchecked")
    public void seed() throws IOException {
        log.info("Loading seed data into database...");

        if (dropAll) dropAll();

        String content = Resources.toString(location, ENCODING);
        Object parsed = JSON.parse(content);

        Assert.state(parsed instanceof DBObject,
                "Parsed object must be instance of DBObject");
        DBObject dbObject = (DBObject) parsed;

        for (String collectionName : dbObject.keySet()) {
            DBCollection collection = mongo.getCollection(collectionName);

            if (skipNonEmptyCollections && collection.count() != 0) {
                log.info("Collection {} is not empty, skipping seed", collectionName);
                continue;
            }
            log.info("Seeding collection: {}", collectionName);
            Object entries = dbObject.get(collectionName);

            Assert.state(entries instanceof List,
                    "There must be list of entries under the collection key");
            collection.insert((List<DBObject>) entries);
        }
    }

    protected void dropAll() {
        for (String name : mongo.getCollectionNames()) {
            if (name.startsWith("system")) continue;

            log.warn("Dropping collection: {}", name);
            mongo.dropCollection(name);
        }
    }


    /**
     * Location of JSON file to import that must follow this structure:
     *
     * <pre>
     *   {
     *     "first_collection_name": [
     *       { entry_1 },
     *       { entry_2 },
     *       ...
     *     ],
     *     "second_collection_name": [
     *       ...
     *     ]
     *   }
     * </pre>
     *
     * @param resource JSON file
     */
    @Required
    public void setLocation(Resource resource) {
        try {
            this.location = resource.getURL();
        } catch (IOException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    @Required
    public void setMongoTemplate(MongoOperations mongoTemplate) {
        this.mongo = mongoTemplate;
    }
}
