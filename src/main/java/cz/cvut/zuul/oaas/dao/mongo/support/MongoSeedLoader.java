package cz.cvut.zuul.oaas.dao.mongo.support;

import com.google.common.io.Files;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@Slf4j
public class MongoSeedLoader {

    private static final Charset ENCODING = Charset.forName("UTF-8");

    private MongoOperations mongo;
    private File location;

    /**
     * Drop all collections before seeding? Default is <tt>false</tt>.
     *
     * @param dropAll
     */
    private @Setter boolean dropAll = false;

    /**
     * Skip import for collections that already exists? Default is <tt>true</tt>.
     *
     * @param skipNonEmptyCollections
     */
    private @Setter boolean skipNonEmptyCollections = true;



    @SuppressWarnings("unchecked")
    public void seed() throws IOException {
        log.info("Loading seed data into database...");

        if (dropAll) dropAll();

        String content = Files.toString(location, ENCODING);
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
            this.location = resource.getFile();
        } catch (IOException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    @Required
    public void setMongoTemplate(MongoOperations mongoTemplate) {
        this.mongo = mongoTemplate;
    }
}
