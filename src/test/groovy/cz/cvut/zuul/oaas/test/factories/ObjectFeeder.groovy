package cz.cvut.zuul.oaas.test.factories

import groovy.util.logging.Slf4j
import org.hibernate.validator.constraints.NotEmpty

import javax.validation.Constraint
import javax.validation.constraints.NotNull
import java.lang.annotation.Annotation
import java.lang.reflect.Field

import static net.java.quickcheck.generator.CombinedGeneratorSamples.anyNullsAnd
import static net.java.quickcheck.generator.PrimitiveGeneratorSamples.*
import static net.java.quickcheck.generator.PrimitiveGenerators.*

/**
 * This class provides methods for populating (domain) objects with random
 * values according to its properties types.
 *
 * Inspired by https://github.com/bodiam/objectmother
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@Slf4j
class ObjectFeeder {

    private ObjectFeeder() {}

    static {
        Class.metaClass.getPropertyFields = {
            def clazz = delegate
            clazz.declaredFields.findAll { field ->
                field.name in clazz.metaClass.properties*.name
            }
        }
    }

    /**
     * Instantiates the given class and populates its properties with a random
     * values according to its type. When the given values map contains
     * key for a property name, then its value will be used instead of
     * generated one.
     *
     * @param clazz the class to create instance of
     * @param values the map of property values to be used [property name : value]
     * @return populated instance of the given class
     */
    static <T> T build(Class<T> clazz, values = [:]) {
        def arguments = [:]

        clazz.propertyFields.each { Field f ->
            arguments[f.name] = values.containsKey(f.name) ? values[f.name] : generateValue(f)
        }
        clazz.newInstance(arguments)
    }

    /**
     * Populates empty properties of the the given object with a random values
     * according to its type.
     *
     * @param object the object to be populated
     * @return the given object
     */
    static <T> T populate(T object) {
        object.class.propertyFields.each { Field f ->
            if (!object[f.name]) {
                object[f.name] = generateValue(f)
            }
        }
        return object
    }

    /**
     * Generates random value of the given field's type.
     *
     * @param field
     * @return generated random value of the field type
     */
    static generateValue(Field field) {
        def notNull = findConstraints(field)*.annotationType().any { type ->
            type in [NotNull, NotEmpty]
        }
        switch (field.type) {
            case Collection:
                return generateList(field, notNull)
            case Map:
                return generateMap(field, notNull)
            default:
                return generateValueOfType(field.type, field, notNull)
        }
    }


    private static Collection generateList(Field field, boolean notNull) {
        Class type = field.genericType.actualTypeArguments[0]

        return new Object[anyInteger(notNull ? 1 : 0, 4)].collect {
            generateValueOfType(type, field)
        }
    }

    private static Map generateMap(Field field, boolean notNull) {
        Class keyType = field.genericType.actualTypeArguments[0]
        Class valueType = field.genericType.actualTypeArguments[1]

        def result = [:]; anyInteger(notNull ? 1 : 0, 4).times {
            result[generateValueOfType(keyType, field, true)] = generateValueOfType(valueType, field, true)
        }
        return result
    }

    private static generateValueOfType(Class type, Field field, boolean notNull = false) {
        switch (type) {
            case String:
                return notNull ? anyLetterString() : anyNullsAnd(letterStrings(), 5)

            case [Integer, int]:
                return anyPositiveInteger()

            case [Double, double]:
                return anyDouble()

            case [Long, long]:
                return anyPositiveLong()

            case [Boolean, boolean]:
                return anyBoolean()

            case Enum:
                return notNull ? anyEnumValue(type) : anyNullsAnd(enumValues(type), 5)

            case Date:
                return notNull ? anyDate() : anyNullsAnd(dates())

            default:
                log.warn("Cannot generate value for property {}.{} of type {}",
                        field.declaringClass.simpleName, field.name, type.simpleName)
                return null
        }
    }

    /**
     * Finds JSR-303 constraint annotations on the given field.
     *
     * @param field
     * @return
     */
    static Annotation[] findConstraints(Field field) {
        field.annotations.findAll { isConstraintAnnotation(it) }
    }

    /**
     * @param annotation
     * @return true if the given annotation is a JSR-303 constraint annotation
     */
    static boolean isConstraintAnnotation(Annotation annotation) {
        Constraint in annotation.annotationType().annotations*.annotationType()
    }
}
