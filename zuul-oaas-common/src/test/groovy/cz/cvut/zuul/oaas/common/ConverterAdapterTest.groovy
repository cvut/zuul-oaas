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

import org.springframework.core.convert.TypeDescriptor
import org.springframework.core.convert.converter.Converter
import org.springframework.core.convert.converter.GenericConverter.ConvertiblePair
import spock.lang.Specification

class ConverterAdapterTest extends Specification {

    def converter = Mock(Converter)
    def adapter = new ConverterAdapter(String, Integer, converter)


    def 'constructor: initializes convertibleTypes with pair of the given types'() {
        setup:
            def exptected = [ new ConvertiblePair(String, Integer) ] as Set
        when:
            def adapter = new ConverterAdapter(String, Integer, {})
        then:
            adapter.convertibleTypes == exptected
    }

    def 'constructor: throws exception when converter is null'() {
        when:
            new ConverterAdapter(String, Integer, null)
        then:
            thrown IllegalArgumentException
    }

    def 'convert: returns null when given null'() {
        setup:
            0 * converter._
        expect:
            adapter.convert(*args) == null
        where:
            args << [ [null, type(String), type(Integer)], [null] ]
    }

    def 'convert: calls adapted converter and returns result'() {
        when:
            def result = adapter.convert(*args)
        then:
            1 * converter.convert('123') >> 123
            result == 123
        where:
            args << [ ['123', type(String), type(Integer)], ['123'] ]
    }

    def 'convert: throws exception when given object is not instance of declared type'() {
        when:
            adapter.convert(*args)
        then:
            thrown IllegalStateException
        where:
            args << [ [123, type(Integer), type(String)], [123] ]
    }


    def type(Class clazz) {
        TypeDescriptor.valueOf(clazz)
    }
}
