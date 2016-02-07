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

import cz.cvut.zuul.oaas.models.Resource
import cz.cvut.zuul.oaas.models.Scope
import cz.cvut.zuul.oaas.models.Visibility
import org.springframework.beans.factory.annotation.Autowired

abstract class ResourcesRepoIT extends BaseRepositoryIT<Resource>{

    @Autowired ResourcesRepo repo


    Resource modifyEntity(Resource entity) {
        entity.name = entity.name?.reverse() ?: 'Awesome API'
        entity.scopes << build(Scope)
        entity
    }


    def 'find all public resources'() {
        setup:
            ([Visibility.PUBLIC] * 3 + [Visibility.HIDDEN] * 2).each { visibility ->
                def entity = build(Resource, [visibility: visibility])
                repo.save(entity)
            }
        expect:
            repo.findAllPublic().size() == 3
    }
}
