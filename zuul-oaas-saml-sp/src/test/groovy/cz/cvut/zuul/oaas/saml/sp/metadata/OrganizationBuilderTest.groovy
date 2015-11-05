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
package cz.cvut.zuul.oaas.saml.sp.metadata

import org.opensaml.saml2.metadata.LocalizedString
import org.opensaml.saml2.metadata.Organization
import spock.lang.Specification

class OrganizationBuilderTest extends Specification {

    def builder = new OrganizationBuilder (
        organizationNames: [cs: 'ČVUT v Praze', en: 'CTU in Prague'],
        displayNames: [cs: 'ČVUT', en: 'CTI'],
        URLs: [cs: 'http://cvut.cz/cs', en: 'http://cvut.cz/en']
    )


    def 'build: creates Organization when all properties are provided'() {
        given:
            def expected = builder
        when:
            def actual = builder.build()
        then:
            actual instanceof Organization
            locStringsToMap(actual.organizationNames*.name) == expected.organizationNames
            locStringsToMap(actual.displayNames*.name) == expected.displayNames
            locStringsToMap(actual.URLs*.getURL()) == expected.URLs
    }

    def 'build: creates empty Organization when no properties are provided'() {
        when:
            def actual = new OrganizationBuilder().build()
        then:
            actual instanceof Organization
            actual.displayNames.empty
            actual.organizationNames.empty
            actual.URLs.empty
    }

    def 'setNames: sets organizationNames'() {
        given:
            def names = [en: 'CUNI']
        when:
            builder.names = names
        then:
            builder.organizationNames == names
    }

    private locStringsToMap(List<LocalizedString> strings) {
        strings.collectEntries { [(it.language): it.localString] }
    }
}
