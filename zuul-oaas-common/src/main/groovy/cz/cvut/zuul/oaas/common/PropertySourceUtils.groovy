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

import org.springframework.core.env.EnumerablePropertySource
import org.springframework.core.env.PropertySources

/**
 * Utility methods for manipulating PropertySource(s).
 *
 * @see org.springframework.core.env.PropertySource PropertySource
 * @see PropertySources
 */
abstract class PropertySourceUtils {

    /**
     * Returns a map of all values from the specified {@link PropertySources} that start
     * with a particular key.
     *
     * <h2>Example</h2>
     *
     * Suppose we have the following properties:
     * <pre>
     * auth.ldap.host = localhost
     * auth.ldap.port = 636
     * auth.ldap.mapping.name = cn
     * </pre>
     *
     * We can extract "subproperties" of <tt>auth.ldap</tt> using keyPrefix
     * <tt>auth.ldap.</tt> (note the leading dot):
     * <pre>
     * [
     *   host: "localhost",
     *   port: 636,
     *   "mapping.name": "cn"
     * ]
     * </pre>
     *
     * @param propertySources The property sources to scan.
     * @param keyPrefix The key prefix to test.
     * @return A map of all sub properties starting with the specified key prefix.
     *
     * @see #subProperties(EnumerablePropertySource, String)
     */
    static Map<String, Object> subProperties(PropertySources propertySources, String keyPrefix) {
        propertySources
            .findAll { it instanceof EnumerablePropertySource }
            .reverse()
            .collectEntries { subProperties((EnumerablePropertySource) it, keyPrefix) }
    }

    /**
     * @see #subProperties(PropertySources, String)
     */
    static Map<String, Object> subProperties(EnumerablePropertySource propertySource, String keyPrefix) {
        propertySource.propertyNames
            .findAll { it.startsWith(keyPrefix) }
            .collect { it.substring(keyPrefix.length()) }
            .collectEntries { [(it): propertySource.getProperty(keyPrefix + it)] }
    }

    /**
     * Returns a list of maps with all sub properties starting with the specified key
     * prefix followed by a numeric index in brackets and a dot (e.g. {@code [1].}).
     *
     * <p>Note: This method uses the same notation for indexes as Spring Boot's
     * {@code @ConfigurationProperties}.</p>
     *
     * <h2>Example</h2>
     *
     * Suppose we have the following properties:
     * <pre>
     * event.attendee[0].name = Kevin Flynn
     * event.attendee[0].email = kevin@flynn.com
     * event.attendee[1].name = Sam Flynn
     * event.attendee[1].phone.work = 555-101-010
     * event.attendee[3].name = Quorra
     * </pre>
     *
     * We can extract attendees as a list of maps using keyPrefix {@code event.attendee}:
     * <pre>
     * [
     *   [name: "Kevin Flynn", email: "kevin@flynn.com"]
     *   [name: "Sam Flynn", "phone.work": "555-101-010"]
     *   null,
     *   [name: "Quorra"]
     * ]
     * </pre>
     *
     * @param propertySources The property source to scan.
     * @param keyPrefix The key prefixes to test.
     * @return A list of maps with all sub properties starting with the specified key
     *         prefix followed by a numeric index in brackets and a dot.
     */
    static List<Map<String, Object>> subPropertiesList(PropertySources propertySources, String keyPrefix) {
        propertiesList( subProperties(propertySources, keyPrefix) )
    }

    /**
     * @see #subProperties(PropertySources, String)
     */
    static List<Map<String, Object>> subPropertiesList(
            EnumerablePropertySource propertySource, String keyPrefix) {

        propertiesList( subProperties(propertySource, keyPrefix) )
    }


    private static List<Map<String, Object>> propertiesList(Map<String, ?> properties) {

        def result = [].withDefault { [:] }

        properties
            .collect { k, v -> [ *parseIndexedPath(k), v ] }
            .findAll { it.size() > 1 }
            .each { idx, key, value ->
            result[idx][key] = value
        }
        result
    }

    private static parseIndexedPath(String propertyName) {
        def (first, rest) = propertyName.split('\\.', 2).toList()

        if (first ==~ /^\[(\d+)\]$/) {
            [ first[1..-2] as int, rest ]
        } else {
            []
        }
    }
}
