package cz.cvut.zuul.oaas.test

import static net.java.quickcheck.generator.PrimitiveGeneratorSamples.anyInteger

/**
 * Factory for building testing (domain) objects.
 *
 * TODO refactor me!
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
class ObjectFactory {

    private Map<Class, Closure> builders = [:]

    /**
     * Registers builder for the given class.
     *
     * @param clazz The class under which will be registered.
     * @param closure The closure that builds instance of the class.
     */
    def <T> void registerBuilder(Class<T> clazz, Closure<? extends T> closure) {
        builders[clazz] = closure
    }

    /**
     * Registers superclass key for the already registered builder.
     *
     * @param superClass The superclass to register.
     * @param registeredClass The class that has already registered builder.
     */
    def <T> void registerSuperclass(Class<T> superClass, Class<? extends T> registeredClass) {
        builders[superClass] = builders[registeredClass]
    }

    /**
     * Builds instance of the given class, populated with random values.
     *
     * @param clazz The type of object to build.
     * @param values The values to pass to the builder.
     * @return
     */
    def <T> T build(Class<T> clazz, Map<String, Object> values = [:]) {
        if (builders[clazz]) {
            builders[clazz].call(values)
        } else {
            ObjectFeeder.build(clazz, values)
        }
    }

    /**
     * Builds list of instances of the given class, populated with random values.
     *
     * @param clazz The type of object to build.
     * @param minSize The minimal size of the list to generate (default is 1).
     * @param maxSize The maximal size of the list to generate (default is 3).
     * @param values The values to pass to the builder (optional).
     * @return
     */
    def <T> List<T> buildListOf(Class<T> clazz, int minSize = 1, int maxSize = 3, Map<String, Object> values = [:]) {
        def size = anyInteger(minSize, maxSize)
        new T[ size ].collect { build(clazz, values) }
    }

    /**
     * @see #buildListOf(Class, int, int, Map)
     */
    def <T> List<T> buildListOf(Class<T> clazz, Map<String, Object> values) {
        buildListOf(clazz, 1, 3, values)
    }
}
