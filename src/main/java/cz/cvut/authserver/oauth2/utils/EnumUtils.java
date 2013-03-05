package cz.cvut.authserver.oauth2.utils;

import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public final class EnumUtils {

    public static <T extends Enum<T>> List<T> valuesOf(Collection<String> names, Class<T> enumClass) {
        Assert.notNull(names);
        List<T> result = new ArrayList<>(names.size());

        for (String name : names) {
            result.add(Enum.valueOf(enumClass, name.toUpperCase()));
        }
        return result;
    }

    private EnumUtils() {}
}
