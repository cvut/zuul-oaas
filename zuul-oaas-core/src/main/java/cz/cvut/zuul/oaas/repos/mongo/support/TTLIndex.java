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

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.index.IndexDefinition;

import java.util.LinkedHashMap;
import java.util.Map;

public class TTLIndex implements IndexDefinition {

    private final Map<String, Direction> fieldSpec = new LinkedHashMap<>();

    private String name;

    private int expireAfterSeconds = 0;


    public TTLIndex(String key, Direction direction) {
        fieldSpec.put(key, direction);
    }


    public TTLIndex named(String name) {
        this.name = name;
        return this;
    }

    /**
     * Configures the number of seconds after which the document should expire.
     * If the value is 0, then the indexed field is considered as an expiration
     * date. Defaults to -1 for no expiry.
     *
     * @see http://docs.mongodb.org/manual/tutorial/expire-data/
     */
    public TTLIndex expireAfterSeconds(int expireAfterSeconds) {
        this.expireAfterSeconds = expireAfterSeconds;
        return this;
    }

    public DBObject getIndexKeys() {
        DBObject dbo = new BasicDBObject();
        for (String k : fieldSpec.keySet()) {
            dbo.put(k, fieldSpec.get(k).equals(Direction.ASC) ? 1 : -1);
        }
        return dbo;
    }

    public DBObject getIndexOptions() {
        DBObject dbo = new BasicDBObject();

        if (name != null) {
            dbo.put("name", name);
        }
        if (expireAfterSeconds > -1) {
            dbo.put("expireAfterSeconds", expireAfterSeconds);
        }
        return dbo;
    }

    @Override
    public String toString() {
        return String.format("Index: %s - Options: %s", getIndexKeys(), getIndexOptions());
    }
}
