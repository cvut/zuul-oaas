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
package cz.cvut.zuul.oaas.repos

import cz.cvut.zuul.oaas.models.Authenticated
import cz.cvut.zuul.oaas.models.Timestamped
import cz.cvut.zuul.oaas.test.CoreObjectFactory
import cz.cvut.zuul.oaas.test.SharedAsserts
import org.apache.commons.lang3.reflect.TypeUtils
import org.springframework.data.domain.Persistable

import static cz.cvut.zuul.oaas.test.Assertions.assertThat

trait EntityTestSupport<E extends Persistable> {

    @Delegate
    @SuppressWarnings('GroovyUnusedDeclaration')
    private final CoreObjectFactory factory = new CoreObjectFactory()

    final Class<E> entityClass =
        TypeUtils.getTypeArguments(this.class, EntityTestSupport).values()[0] as Class<E>



    def E buildEntity() {
        build(entityClass)
    }

    def List<E> buildEntities(count) {
        new Object[count].collect {
            buildEntity()
        }
    }

    def List<E> seed() {
        buildEntities(3)
    }

    void assertIt(E actual, E expected) {
        def excludedProps = ['new']

        if (Timestamped.isAssignableFrom(entityClass)) {
            excludedProps += ['createdAt', 'updatedAt']
        }
        if (Authenticated.isAssignableFrom(entityClass)) {
            excludedProps << 'authentication'
            SharedAsserts.isEqual(actual.authentication, expected.authentication)
        }
        assertThat( actual ).equalsTo( expected ).inAllPropertiesExcept( *excludedProps )
    }
}
