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

import org.opensaml.saml2.metadata.ContactPerson
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class ContactPersonBuilderTest extends Specification {

    def builder = new ContactPersonBuilder(
        company: 'ENCOM',
        emailAddresses: ['flynn@encom.com'],
        givenName: 'Kevin',
        surName: 'Flynn',
        telephoneNumbers: ['555-101-010'],
        type: ContactPersonType.TECHNICAL
    )


    def 'build: creates ContactPerson when all properties are provided'() {
        given:
            def expected = builder
        when:
            def actual = builder.build()
        then:
            actual instanceof ContactPerson
            actual.company.name == expected.company
            actual.emailAddresses*.address == expected.emailAddresses
            actual.givenName.name == expected.givenName
            actual.surName.name == expected.surName
            actual.telephoneNumbers*.number == expected.telephoneNumbers
            actual.type.toString() == expected.type.toString().toLowerCase()
    }

    def 'build: creates empty ContactPerson when no properties are provided'() {
        when:
            def actual = new ContactPersonBuilder().build()
        then:
            actual instanceof ContactPerson
            actual.company == null
            actual.emailAddresses.empty
            actual.givenName == null
            actual.surName == null
            actual.telephoneNumbers.empty
            actual.type == null
    }

    def 'setEmail: sets emailAddresses to #desc'() {
        when:
            builder.email = value
        then:
            builder.emailAddresses == expected
        where:
            value             || expected            | desc
            'kevin@flynn.com' || ['kevin@flynn.com'] | 'given value when non-null'
            null              || []                  | 'empty list when null is given'
    }

    def 'setPhone: sets telephoneNumbers to #desc'() {
        when:
            builder.phone = value
        then:
            builder.telephoneNumbers == expected
        where:
            value         || expected        | desc
            '555-123-456' || ['555-123-456'] | 'given value when non-null'
            null          || []              | 'empty list when null is given'
    }

    def 'setName: splits name to givenName and surname: #name'() {
        when:
            def builder = new ContactPersonBuilder(name: name)
        then:
            builder.givenName == givenName
            builder.surName == surname
        where:
            name              || givenName | surname
            'Quorra'          || 'Quorra'  | null
            'Kevin Flynn'     || 'Kevin'   | 'Flynn'
            'Emmett L. Brown' || 'Emmett'  | 'L. Brown'
    }

    def 'setType(String): parses value as ContactPersonType: #value -> #expected'() {
        when:
            builder.type = value
        then:
            builder.type == expected
        where:
            value     || expected
            'SUPPORT' || ContactPersonType.SUPPORT
            'support' || ContactPersonType.SUPPORT
            'OtHeR'   || ContactPersonType.OTHER
            null      || null
    }
}
