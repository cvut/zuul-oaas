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

import cz.cvut.zuul.oaas.models.Timestamped
import spock.lang.Specification

class TimestampedWriterTest extends Specification {

    def entity = new TimestampedImpl()
    def date = new Date(123456789)


    def 'setCreatedAt: sets createdAt to given date'() {
        when:
            TimestampedWriter.setCreatedAt(entity, date)
        then:
            entity.createdAt == date
    }

    def 'setUpdatedAt: sets updatedAt to given date'() {
        when:
            TimestampedWriter.setUpdatedAt(entity, date)
        then:
            entity.updatedAt == date
    }

    static class TimestampedImpl implements Timestamped {}
}
