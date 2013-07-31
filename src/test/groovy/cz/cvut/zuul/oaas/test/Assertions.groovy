package cz.cvut.zuul.oaas.test

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
class Assertions {

    static ObjectAssert assertThat(actual) {
        new ObjectAssert(actual)
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
     * Asserts all common properties of the actual and expected object, except
     * the given excluded properties.
     *
     * @param excludedProperties property names to exclude from assertion
     */
    void inAllPropertiesExcept(String... excludedProperties) {
        def properties = actual.properties.keySet() intersect expected.properties.keySet()
        properties.removeAll { it in excludedProperties + 'class' }

        properties.each { String property ->
            assert actual[ property ] == expected[ property ]
        }
    }
}
