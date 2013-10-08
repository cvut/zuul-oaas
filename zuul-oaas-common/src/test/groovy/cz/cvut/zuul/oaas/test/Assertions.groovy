package cz.cvut.zuul.oaas.test

import static cz.cvut.zuul.oaas.test.Assertions.assertThat

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
class Assertions {

    static ObjectAssert assertThat(actual) {
        new ObjectAssert(actual)
    }

    static CollectionAssert assertThat(Collection actual) {
        new CollectionAssert(actual: actual)
    }
}

class ObjectAssert {

    private final actual

    ObjectAssert(actual) {
        this.actual = actual
    }

    ObjectEqualsAssert equalsTo(expected) {
        new ObjectEqualsAssert(actual, expected)
    }
}

class CollectionAssert {
    private Collection actual

    CollectionEqualsAssert equalsTo(Collection expected) {
        new CollectionEqualsAssert(actual: actual, expected: expected)
    }
}

class ObjectEqualsAssert {

    private final actual, expected

    ObjectEqualsAssert(actual, expected) {
        this.actual = actual
        this.expected = expected
    }

    /**
     * Asserts all common properties of the actual and expected object.
     */
    void inAllProperties() {
        inAllPropertiesExcept()
    }

    /**
     * Asserts all properties of the expected object against the actual object,
     * except the given excluded properties.
     *
     * @param excludedProperties Property names to exclude from assertion.
     */
    void inAllPropertiesExcept(String... excludedProperties) {
        def properties = expected.properties.keySet()
        properties.removeAll { it in excludedProperties + 'class' }

        properties.each { prop -> assertProperties(prop) }
    }

    /**
     * Asserts specified properties.
     *
     * @param includedProperties Property names to assert.
     */
    void inProperties(String... includedProperties) {
        includedProperties.each { prop -> assertProperties(prop) }
    }

    private assertProperties(String property) {
        if (actual[property] instanceof Set || expected[property] instanceof Set) {
            assert actual[ property ] as Set == expected[ property ] as Set
        } else {
            assert actual[ property ] == expected[ property ]
        }
    }
}

class CollectionEqualsAssert {
    private Collection actual, expected

    void inAllProperties() {
        inAllPropertiesExcept()
    }

    void inAllPropertiesExcept(String... excludedProperties) {
        [actual, expected].transpose().each { a, e ->
            assertThat(a).equalsTo(e).inAllPropertiesExcept(excludedProperties)
        }
    }
}
