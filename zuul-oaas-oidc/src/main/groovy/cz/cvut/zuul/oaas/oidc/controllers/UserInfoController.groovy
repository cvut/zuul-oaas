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
package cz.cvut.zuul.oaas.oidc.controllers

import cz.cvut.zuul.oaas.models.User
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import java.security.Principal

import static org.springframework.web.bind.annotation.RequestMethod.GET

// TODO this is dumb, temporary implementation!
@RestController
@RequestMapping('/oauth/userinfo')
class UserInfoController {

    @RequestMapping(method = GET)
    def getUserInfo(Principal principal) {

        def user = extractUserPrincipal((OAuth2Authentication) principal)
        [
            username: user.username,
            email: user.email
        ]
    }


    protected extractUserPrincipal(OAuth2Authentication authentication) {
        def object = authentication.userAuthentication.principal
        if (object instanceof User) {
            return (User) object;
        }
        throw new IllegalStateException('User authentication could not be converted to User')
    }
}
