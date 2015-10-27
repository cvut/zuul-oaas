/*
 * The MIT License
 *
 * Copyright 2013-2015 Czech Technical University in Prague.
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
package cz.cvut.zuul.oaas.web.support

import cz.cvut.zuul.oaas.web.support.SpringEnvironmentExpressionDialect.EnvironmentMapAdapter
import org.springframework.mock.env.MockEnvironment
import org.thymeleaf.context.IProcessingContext
import spock.lang.Specification
import spock.lang.Unroll

class SpringEnvironmentExpressionDialectTest extends Specification {

    def environment = new MockEnvironment().withProperty('answer', '42')
    def subject = new SpringEnvironmentExpressionDialect(environment)


    def 'getAdditionalExpressionObjects: returns map with EnvironmentMapAdapter at key "env"'() {
        given:
            def processingContext = Mock(IProcessingContext)
        when:
            def actual = subject.getAdditionalExpressionObjects(processingContext)
        then:
            actual.keySet() == ['env'] as Set
            actual['env'] instanceof EnvironmentMapAdapter
            actual['env']['answer'] == '42'
    }


    @Unroll
    static class EnvironmentMapAdapterTest extends Specification {

        def environment = new MockEnvironment().withProperty('answer', '42')
        def adapter = new EnvironmentMapAdapter(environment)

        def toStringKey = new Object() {
            String toString() { 'answer' }
        }


        def 'get#propertyName: delegates to underlying environment'() {
            setup:
                environment./set${propertyName}/('dev', 'test')
            expect:
                adapter./get${propertyName}/() as Set == ['dev', 'test'] as Set
            where:
                propertyName << ['ActiveProfiles', 'DefaultProfiles']
        }

        def 'acceptsProfiles: delegates to underlying environment'() {
            setup:
                environment.setDefaultProfiles('dev', 'test')
            expect:
                adapter.acceptsProfiles('dev')
                ! adapter.acceptsProfiles('foo')
        }

        def 'isEmpty: returns false'() {
            expect: ! adapter.isEmpty()
        }

        def 'containsValue: returns false'() {
            expect: ! adapter.containsValue('anything')
        }

        def 'containsKey: returns true when property key exists in environment'() {
            expect: adapter.containsKey('answer')
        }

        def "containsKey: returns false when property key doesn't exist in environment"() {
            expect: ! adapter.containsKey('unknown')
        }

        def 'containsKey: calls toString() on given key if not instance of String'() {
            expect: adapter.containsKey(toStringKey)
        }

        def "get: returns property's value from environment when given existing property key"() {
            expect: adapter.get('answer') == '42'
        }

        def "get: returns null when given property key that doesn't exist in environment"() {
            expect: adapter.get('unknown') == null
        }

        def 'get: calls toString() on given key if not instance of String'() {
            expect: adapter.get(toStringKey) == '42'
        }

        def "#methodName: throws UnsupportedOperationException"() {
            when:
                adapter./${methodName}/(*args)
            then:
                thrown UnsupportedOperationException
            where:
                method             | args
                Map.&size          | []
                Map.&put           | ['foo', 'bar']
                Map.&remove        | ['remove']
                Map.&putAll        | [[foo: 'bar']]
                Map.&clear         | []
                Map.&keySet        | []
                Map.&values        | []
                Map.&entrySet      | []

                methodName = method.method
        }
    }
}
