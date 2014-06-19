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
package cz.cvut.zuul.oaas.restapi.config

import cz.cvut.zuul.oaas.api.services.ClientsService
import cz.cvut.zuul.oaas.api.services.ResourcesService
import cz.cvut.zuul.oaas.api.services.TokensService
import cz.cvut.zuul.oaas.common.config.ConfigurationSupport
import cz.cvut.zuul.oaas.restapi.controllers.ClientsController
import cz.cvut.zuul.oaas.restapi.controllers.ResourcesController
import cz.cvut.zuul.oaas.restapi.controllers.TokenInfoController
import cz.cvut.zuul.oaas.restapi.controllers.TokensController
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import javax.inject.Inject

@Configuration
class RestControllersConfig extends ConfigurationSupport {

    // external services
    @Inject ClientsService clientsService
    @Inject ResourcesService resourcesService
    @Inject TokensService tokensService


    @Bean ClientsController clientsController() {
        new ClientsController (
            clientsService: clientsService
        )
    }

    @Bean ResourcesController resourcesController() {
        new ResourcesController (
            resourceService: resourcesService
        )
    }

    @Bean TokensController tokensController() {
        new TokensController (
            tokensService: tokensService
        )
    }

    @Bean TokenInfoController tokenInfoController() {
        new TokenInfoController (
            tokensService: tokensService,
            cacheMaxAge:   $('restapi.tokeninfo.cache.maxage') as int
        )
    }
}
