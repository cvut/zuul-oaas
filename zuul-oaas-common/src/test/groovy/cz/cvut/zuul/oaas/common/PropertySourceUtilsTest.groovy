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
package cz.cvut.zuul.oaas.common

import org.springframework.core.env.MutablePropertySources
import org.springframework.core.env.PropertySource
import org.springframework.core.env.PropertySource.StubPropertySource
import org.springframework.mock.env.MockPropertySource
import spock.lang.Specification

import static cz.cvut.zuul.oaas.common.PropertySourceUtils.subProperties
import static cz.cvut.zuul.oaas.common.PropertySourceUtils.subPropertiesList

class PropertySourceUtilsTest extends Specification {


    def 'subProperties: returns map of values from the source which key starts with the prefix'() {
        given:
            def propSource = new MockPropertySource([
                'foo.bar.first': 1,
                'foo.bar.second': 2,
                'foo.baz': 10
            ] as Properties)
            def propSources = propertySources(propSource)
        expect:
            subProperties(propSource, prefix) == expected
            subProperties(propSources, prefix) == expected
        where:
            prefix     | expected
            'foo.bar.' | [first: 1, second: 2]
            'foo.'     | ['bar.first': 1, 'bar.second': 2, baz: 10]
            'invalid'  | [:]
    }

    def 'subProperties(PropertySources): resolves properties from the sources in correct order'() {
        setup:
            def propSources = propertySources(
                new MockPropertySource('first', [
                    'foo.alpha.first': 1,
                    'foo.both': 42
                ] as Properties),
                new MockPropertySource('second', [
                    'foo.alpha.second': 2,
                    'foo.beta': 3,
                    'foo.both': 66
                ] as Properties)
            )
        and:
            def expected = [
                'alpha.first': 1,
                'alpha.second': 2,
                'beta': 3,
                'both': 42
            ]
        expect:
            subProperties(propSources, 'foo.') == expected
    }

    def 'subProperties(PropertySources): ignores non-enumerable property source'() {
        setup:
            def propSources = propertySources(
                new MockPropertySource('first'),
                new StubPropertySource('stub')
            )
        expect:
            subProperties(propSources, 'prefix') == [:]
    }


    def 'subPropertiesList: returns list of maps with sub properties starting with the prefix and index'() {
        given:
            def propSource = new MockPropertySource([
                'alpha.beta[0].foo': 1,
                'alpha.beta[0].bar': 2,
                'alpha.beta[1].foo.bar': 42,
                'alpha.beta[11].foo': 11,
                'alpha.beta.meh': 66,
                'alpha.beta[6]x.wtf': 66,
                'alpha.beta[6x].wtf': 66
            ] as Properties)
            def propSources = propertySources(propSource)
        and:
            def expected = [[foo: 1, bar: 2], ['foo.bar': 42]]
            expected[11] = [foo: 11]
        expect:
            subPropertiesList(propSource, 'alpha.beta') == expected
            subPropertiesList(propSources, 'alpha.beta') == expected
    }


    def propertySources(PropertySource ...propertySources) {
        new MutablePropertySources().with { res ->
            propertySources.each { res.addLast(it) }; res
        }
    }
}
