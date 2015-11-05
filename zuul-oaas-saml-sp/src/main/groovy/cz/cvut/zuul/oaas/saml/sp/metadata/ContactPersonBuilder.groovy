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

import org.opensaml.saml2.metadata.Company
import org.opensaml.saml2.metadata.ContactPerson
import org.opensaml.saml2.metadata.EmailAddress
import org.opensaml.saml2.metadata.GivenName
import org.opensaml.saml2.metadata.SurName
import org.opensaml.saml2.metadata.TelephoneNumber

/**
 * Builder of {@link ContactPerson}.
 */
class ContactPersonBuilder extends SamlObjectBuilder {

    /** @see ContactPerson#getCompany() */
    String company

    /** @see ContactPerson#getEmailAddresses() */
    List<String> emailAddresses = []

    /** @see ContactPerson#getGivenName() */
    String givenName

    /** @see ContactPerson#getSurName() */
    String surName

    /** @see ContactPerson#getTelephoneNumbers() */
    List<String> telephoneNumbers = []

    /** @see ContactPerson#getType() */
    ContactPersonType type


    ContactPerson build() {
        build ContactPerson, { p ->
            if (company) {
                p.company = build Company, { it.name = company }
            }
            if (givenName) {
                p.givenName = build GivenName, { it.name = givenName }
            }
            if (surName) {
                p.surName = build SurName, { it.name = surName }
            }
            p.emailAddresses.addAll(emailAddresses.collect { address ->
                build EmailAddress, { it.address = address }
            })
            p.telephoneNumbers.addAll(telephoneNumbers.collect { number ->
                build TelephoneNumber, { it.number = number }
            })
            p.type = type?.toSaml()
        }
    }

    /**
     * Sets the email address of this person.
     * This is a shorthand for {@link #emailAddresses}.
     */
    void setEmail(String address) {
        emailAddresses = address ? [address] : []
    }

    /**
     * Splits the given full name to {@link #givenName} and {@link #surName}.
     */
    void setName(String fullName) {
        (givenName, surName) = fullName.split(' ', 2).toList()
    }

    /**
     * Sets the phone number of this person.
     * This is a shorthand for {@link #telephoneNumbers}.
     */
    void setPhone(String number) {
        telephoneNumbers = number ? [number] : []
    }

    void setType(ContactPersonType type) {
        this.type = type
    }

    void setType(String type) {
        this.type = type ? ContactPersonType.valueOf(type.toUpperCase()) : null
    }
}
