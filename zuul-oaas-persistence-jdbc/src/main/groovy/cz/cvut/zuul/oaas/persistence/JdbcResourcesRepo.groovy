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
package cz.cvut.zuul.oaas.persistence

import cz.cvut.zuul.oaas.common.JSON
import cz.cvut.zuul.oaas.models.Resource
import cz.cvut.zuul.oaas.models.Scope
import cz.cvut.zuul.oaas.models.Visibility
import cz.cvut.zuul.oaas.repos.ResourcesRepo
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors

@CompileStatic
@InheritConstructors
class JdbcResourcesRepo extends AbstractJdbcRepository<Resource, String> implements ResourcesRepo {

    final tableName = 'resources'


    List<Resource> findAllPublic() {
        findBy visibility: Visibility.PUBLIC.name()
    }


    //////// ResultSet Mapping ////////

    Resource mapRow(Map row) {
        new Resource (
            id:          row.id as String,
            baseUrl:     row.base_url as String,
            description: row.description as String,
            name:        row.name as String,
            version:     row.version as String,
            scopes:      deserializeScopes(row.scopes as String),
            visibility:  row.visibility as Visibility
        )
    }

    Map mapColumns(Resource obj) {
        [
            id:          obj.id,
            base_url:    obj.baseUrl,
            description: obj.description,
            name:        obj.name,
            version:     obj.version,
            scopes:      serializeScopes(obj.scopes),
            visibility:  obj.visibility.name()
        ]
    }


    // TODO: temporary solution
    private Set<Scope> deserializeScopes(String json) {
        JSON.parse(json).collect { key, val ->
            new Scope (
                name: key as String,
                description: ((Map) val).description as String,
                secured: ((Map) val).secured as boolean
            )
        } as Set
    }

    // TODO: temporary solution
    private String serializeScopes(Set<Scope> scopes) {
        JSON.serialize( scopes.collectEntries { scope ->
            [(scope.name): [description: scope.description, secured: scope.secured]]
        })
    }
}
