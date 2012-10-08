package cz.cvut.authserver.oauth2.mongo;

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
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
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
        return list != null ? StringUtils.toStringArray(list) : null;
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
