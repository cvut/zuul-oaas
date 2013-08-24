package cz.cvut.zuul.oaas.support

import ma.glasnost.orika.metadata.TypeFactory
import spock.lang.Specification

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
class CaseInsensitiveToEnumConverterTest extends Specification {

    def converter = new CaseInsensitiveToEnumConverter()


    def 'can convert String to Enum types'() {
        expect:
            converter.canConvert(TypeFactory.valueOf(String), TypeFactory.valueOf(TestEnum))
    }

    def 'cannot convert unsupported types'() {
        expect:
            ! converter.canConvert(TypeFactory.valueOf(sourceType), TypeFactory.valueOf(targetType))
        where:
            sourceType | targetType
            TestEnum   | String
            String     | String
            Enum       | TestEnum
            Integer    | TestEnum
    }

    def 'convert string value to enum type case insensitively'() {
        expect:
            converter.convert(source, TypeFactory.valueOf(TestEnum)) == expected
        where:
            source | expected
            'FOO'  | TestEnum.FOO
            'foo'  | TestEnum.FOO
            'bAr'  | TestEnum.BAR
    }


    enum TestEnum {
        FOO, BAR, BAZ
    }
}
