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
package cz.cvut.zuul.oaas.it.support

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

import java.util.regex.Pattern

class HttpResponseAssertDSL {

    void expect(Map<String, ?> kwargs, MyResponseEntity response) {

        if ('status' in kwargs) {
            switch (kwargs['status']) {
                case HttpStatus:
                    assert response.statusCode == kwargs['status']; break
                default:
                    assert response.status == kwargs['status']; break
            }
        }

        collectHeaders(kwargs).each { String header, expected ->
            switch (expected) {
                case Closure:
                    (expected as Closure).call(response.headers.getFirst(header)); break
                case Pattern:
                    assert response.headers[header] ==~ expected; break
                case MediaType:
                    def actual = MediaType.valueOf(response.headers.getFirst(header))
                    assert expected.includes( actual ); break
                case List:
                    assert response.headers[header] == expected; break
                default:
                    assert response.headers.getFirst(header) == expected
            }
        }

        if ('body' in kwargs) {
            (kwargs['body'] as Closure).call(response.body)
        }
    }

    private collectHeaders(Map<String, ?> kwargs) {

        kwargs.findAll { k, v ->
            k.chars[0].isUpperCase()
        }.collectEntries([:]) { k, v ->
            k = k.replaceAll(/\B[A-Z]/) { '-' + it }
            [(k): v]
        }
    }
}
