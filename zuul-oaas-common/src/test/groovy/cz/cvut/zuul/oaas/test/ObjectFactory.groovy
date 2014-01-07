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
package cz.cvut.zuul.oaas.test

import static net.java.quickcheck.generator.PrimitiveGeneratorSamples.anyInteger

/**
 * Factory for building testing (domain) objects.
 *
 * TODO refactor me!
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
