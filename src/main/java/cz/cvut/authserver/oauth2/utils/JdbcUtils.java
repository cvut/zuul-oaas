package cz.cvut.authserver.oauth2.utils;

import org.apache.commons.lang.SerializationUtils;

import java.io.Serializable;
import java.sql.Array;
import java.sql.SQLException;
import java.util.*;

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public final class JdbcUtils {

    public static List<String> toList(Array array) throws SQLException {
        List<String> result = new ArrayList<>();

        if (array != null) {
            for (Object o : (Object[]) array.getArray()) {
                result.add((String) o);
            }
            array.free();
        }
        return result;
    }

    public static Set<String> toSet(Array array) throws SQLException {
        return new LinkedHashSet<>(toList(array));
    }

    public static Object[] toArray(Collection<?> items) {
        if (items == null) return new Object[]{};
        return items.toArray();
    }


    public static byte[] serialize(Serializable object) {
        if (object == null) return new byte[0];
        return SerializationUtils.serialize(object);
    }

    @SuppressWarnings("unchecked")
    public static <T> T deserialize(byte[] bytes, Class<T> type) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        Object object = SerializationUtils.deserialize(bytes);

        if (! type.isInstance(object)) {
            throw new IllegalArgumentException("Deserialized object cannot be cast to: " + type.getName());
        }
        return (T) SerializationUtils.deserialize(bytes);
    }

    private JdbcUtils() {}
}
