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

import cz.cvut.zuul.oaas.common.DateUtils

import java.sql.Timestamp

abstract class TimestampUtils {

    private static final SQL_DATE_INFINITY_BOUNDARY = Date.parse('yyyy-MM-dd', '9999-12-31')


    /**
     * Converts the SQL Timestamp to Java Date and handles "infinity".
     * If the given timestamp represents date after 9999-12-31, then it returns
     * {@link DateUtils#END_OF_TIME END_OF_TIME}.
     *
     * <p>Note: SQL databases handles infinite timestamp differently, so we
     * can't rely on any exact value here.</p>
     */
    static Date convertTimestamp(Timestamp timestamp) {
        timestamp.after(SQL_DATE_INFINITY_BOUNDARY) ? DateUtils.END_OF_TIME : timestamp
    }
}
