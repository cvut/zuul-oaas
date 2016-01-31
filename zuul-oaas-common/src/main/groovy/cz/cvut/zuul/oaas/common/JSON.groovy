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

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import org.springframework.util.Assert

import static groovy.json.JsonParserType.INDEX_OVERLAY

@CompileStatic
abstract class JSON {

    private static final JsonSlurper JSON_SLURPER = new JsonSlurper(type: INDEX_OVERLAY)


    /**
     * Parses a textual representation of a JSON object. If the given text does
     * not represent a JSON <i>object</i> (but an array, null, ...), then it
     * throws {@link IllegalArgumentException}. If the given text is Java null
     * or a blank string, then it returns an empty map.
     *
     * <p>This method uses Groovy's {@link JsonSlurper} with Index Overlay
     * parser.  Read {@link groovy.json.JsonParserType#INDEX_OVERLAY this} to
     * know its limitations.</p>
     */
    static Map parse(String text) {
        def result = [:]

        if (text && !text.isAllWhitespace()) {
            result = JSON_SLURPER.parseText(text)
            Assert.isInstanceOf(Map, result, 'The given text is not a JSON object')
        }
        (Map) result
    }

    /**
     * Serializes the given Map to JSON and returns its textual representation.
     * If the given map is null, then it's treated as an empty map.
     */
    static String serialize(Map map) {
        JsonOutput.toJson(map ?: [:])
    }
}
