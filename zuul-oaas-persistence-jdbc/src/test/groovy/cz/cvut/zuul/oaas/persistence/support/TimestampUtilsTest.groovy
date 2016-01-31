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
package cz.cvut.zuul.oaas.persistence.support

import spock.lang.Specification
import spock.lang.Unroll

import java.sql.Timestamp

import static cz.cvut.zuul.oaas.common.DateUtils.END_OF_TIME

@Unroll
class TimestampUtilsTest extends Specification {

    static final DATE_FORMAT = 'yyyy-MM-dd'


    def "convertTimestamp: converts #inputDesc to #expectedDesc"() {
        expect:
            TimestampUtils.convertTimestamp(input) == expected
        where:
            input             | expected
            ts('2016-01-31')  | date('2016-01-31')
            ts('9999-12-31')  | date('9999-12-31')
            ts('10000-01-01') | END_OF_TIME
            ts('90006-06-06') | END_OF_TIME

            inputDesc = input.format(DATE_FORMAT)
            expectedDesc = expected == END_OF_TIME ? 'END_OF_TIME' : expected.format(DATE_FORMAT)
    }


    static date(str) {
        Date.parse(DATE_FORMAT, str)
    }

    static ts(str) {
        new Timestamp(date(str).time)
    }
}
