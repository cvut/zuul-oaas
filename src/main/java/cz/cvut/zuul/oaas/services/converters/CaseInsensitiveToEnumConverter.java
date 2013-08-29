package cz.cvut.zuul.oaas.services.converters;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public class CaseInsensitiveToEnumConverter extends CustomConverter<String, Enum> {

    public boolean canConvert(Type<?> sourceType, Type<?> destinationType) {
        return String.class == sourceType.getRawType()
                && Enum.class.isAssignableFrom(destinationType.getRawType());
    }

    public Enum convert(String source, Type<? extends Enum> destinationType) {
        String name = source.toUpperCase();
        return Enum.valueOf(destinationType.getRawType(), name);
    }
}
