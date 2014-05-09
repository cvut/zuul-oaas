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
package cz.cvut.zuul.oaas.restapi.controllers

import cz.cvut.zuul.oaas.api.models.ClientDTO
import cz.cvut.zuul.oaas.api.services.ClientsService

import javax.inject.Inject

class ClientsControllerIT extends AbstractControllerIT {

    @Inject ClientsController controller

    def clientsService = Mock(ClientsService)


    def 'GET client'() {
        setup:
            1 * clientsService.findClientById('42') >> build(ClientDTO)
        when:
            perform GET('/42')
        then:
            with (response) {
                status == 200
                contentType == CONTENT_TYPE_JSON
                ! json.isEmpty()
            }
    }

    def 'POST client'() {
        setup:
            1 * clientsService.createClient(_ as ClientDTO) >> '123'
        when:
            perform POST('/').with {
                content '{ "redirect_uri": "http://example.org" }'
            }
        then:
            response.status        == 201
            response.redirectedUrl == "${baseUri}/123"
    }

    def 'PUT client with changed clientId'() {
        when:
            perform PUT('/123').with {
                content '{ "client_id": "666" }'
            }
        then:
            response.status == 409
    }

    def 'PUT client'() {
        setup:
            1 * clientsService.updateClient({
                it.productName == 'Skynet'
            })
        when:
            perform PUT('/123').with {
                content """{
                        "client_id": "123",
                        "product_name": "Skynet"
                    }"""
            }
        then:
            response.status == 204
    }

    def 'DELETE client'() {
        setup:
            1 * clientsService.removeClient('123')
        when:
            perform DELETE('/123')
        then:
            response.status == 204
    }
}
