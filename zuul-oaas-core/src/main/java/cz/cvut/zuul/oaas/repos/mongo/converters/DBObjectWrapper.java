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
package cz.cvut.zuul.oaas.repos.mongo.converters;

import com.mongodb.DBObject;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bson.BSONObject;
import org.springframework.util.StringUtils;

/**
 * This is a {@link DBObject} wrapper with typed {@linkplain #get(String)}
 * methods for some ordinary types (explicit casting is used).
 */
public class DBObjectWrapper implements DBObject {

    private final DBObject dbo;


    public DBObjectWrapper(DBObject dbo) {
        this.dbo = dbo;
    }


    public DBObjectWrapper getDBObject(String key) {
        Object value = dbo.get(key);
        return value != null ? new DBObjectWrapper((DBObject) value) : null;
    }

    public String getString(String key) {
        Object value = dbo.get(key);
        return value != null ? (String) value : null;
    }

    public Integer getInteger(String key) {
        Object value = dbo.get(key);
        return value != null ? (Integer) value : null;
    }

    public Boolean getBoolean(String key) {
        Object value = dbo.get(key);
        return value != null ? (Boolean) value : null;
    }

    public Date getDate(String key) {
        Object value = dbo.get(key);
        return value != null ? (Date) value : null;
    }

    public String[] getStringArray(String key) {
        List list = getList(key);
        return list != null ? StringUtils.toStringArray(list) : new String[0];
    }

    public List getList(String key) {
        Object value = dbo.get(key);

        if (value == null) {
            return null;
        } else if (value instanceof List) {
            return (List) value;
        } else {
            return Arrays.asList(value);
        }
    }

    public Set getSet(String key) {
        List list = getList(key);
        return list != null ? new HashSet(list) : null;
    }

    public Map getMap(String key) {
        Object value = dbo.get(key);
        return value != null ? (Map) value : null;
    }



    //////// Delegate to wrapped ////////

    @Override
    public void markAsPartialObject() {
        dbo.markAsPartialObject();
    }

    @Override
    public boolean isPartialObject() {
        return dbo.isPartialObject();
    }

    @Override
    public Object put(String key, Object v) {
        return dbo.put(key, v);
    }

    @Override
    public void putAll(BSONObject o) {
        dbo.putAll(o);
    }

    @Override
    public void putAll(Map m) {
        dbo.putAll(m);
    }

    @Override
    public Object get(String key) {
        return dbo.get(key);
    }

    @Override
    public Map toMap() {
        return dbo.toMap();
    }

    @Override
    public Object removeField(String key) {
        return dbo.removeField(key);
    }

    @Override @Deprecated
    public boolean containsKey(String s) {
        return dbo.containsKey(s);
    }

    @Override
    public boolean containsField(String s) {
        return dbo.containsField(s);
    }

    @Override
    public Set<String> keySet() {
        return dbo.keySet();
    }
}
