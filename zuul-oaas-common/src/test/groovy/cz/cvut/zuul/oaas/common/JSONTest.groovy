/*
 * The MIT License
 *
 * Copyright 2013-2016 Czech Technical University in Prague.
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

import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class JSONTest extends Specification {


    def "parse: returns '#expected' when given '#input'"() {
        expect:
            JSON.parse(input) == expected
        where:
            input       | expected
            '{"a": 42}' | [a: 42]
            '{}'        | [:]
            null        | [:]
    }

    def "parse: throws IllegalArgument exception when given: '#input'"() {
        when:
           JSON.parse(input)
        then:
            thrown IllegalArgumentException
        where:
            input << ['null', 'true', 'false', '66', '["a", "b"]']
    }


    def "serialize: returns '#expected' when given '#input'"() {
        expect:
            JSON.serialize(input) == expected
        where:
            input   | expected
            [a: 42] | '{"a":42}'
            [:]     | '{}'
            null    | '{}'
    }
}
