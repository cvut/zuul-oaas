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
package cz.cvut.zuul.oaas.web.controllers

import cz.cvut.zuul.oaas.api.services.ClientsService
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.oauth2.provider.AuthorizationRequest
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.SessionAttributes
import org.springframework.web.servlet.ModelAndView

/**
 * Controller for retrieving the model for and displaying the confirmation page
 * for access to a protected resource.
 */
@Controller
@SessionAttributes('authorizationRequest')
class AccessConfirmationController {

    ClientsService clientsService


    @RequestMapping('/oauth/confirm_access')
    def getAccessConfirmation(Map model) {
        def clientAuth = model.remove('authorizationRequest') as AuthorizationRequest

        def client = clientsService.findClientById(clientAuth.clientId)

        model.putAll([
            auth_request: clientAuth,
            client: client
        ])

        return new ModelAndView('confirm_access', model)
    }

    @RequestMapping('/oauth/error')
    def handleError() { 'oauth_error' }
}
