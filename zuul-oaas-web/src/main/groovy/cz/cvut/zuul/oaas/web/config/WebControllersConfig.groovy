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
package cz.cvut.zuul.oaas.web.config

import cz.cvut.zuul.oaas.api.services.ClientsService
import cz.cvut.zuul.oaas.common.config.ConfigurationSupport
import cz.cvut.zuul.oaas.web.controllers.AccessConfirmationController
import cz.cvut.zuul.oaas.web.controllers.MainController
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import javax.inject.Inject

@Configuration
class WebControllersConfig extends ConfigurationSupport {

    // external service
    @Inject ClientsService clientsService

    /**
     * Static pages
     */
    @Bean MainController mainController() {
        new MainController()
    }

    /**
     * Overrides the default mappings for approval and error pages.
     */
    @Bean AccessConfirmationController confirmationController() {
        new AccessConfirmationController (
            clientsService: clientsService
        )
    }
}
