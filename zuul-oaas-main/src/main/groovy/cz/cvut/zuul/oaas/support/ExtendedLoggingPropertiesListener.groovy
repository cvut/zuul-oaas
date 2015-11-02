/*
 * The MIT License
 *
 * Copyright 2015 Lukas Hinsch.
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
package cz.cvut.zuul.oaas.support

import org.springframework.boot.bind.RelaxedPropertyResolver
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent
import org.springframework.boot.logging.LoggingApplicationListener
import org.springframework.context.ApplicationEvent

/**
 * Listener to make properties from Spring environment available in logging configuration.
 *
 * <p>It re-uses the concept from Spring boot of setting (system) properties and then
 * reloading the logging configuration. As there is no stable way to hook into the
 * existing reload by Boot the same behavior is triggered once again reloading and
 * initializing the logging framework twice (which would not be necessary if code like
 * this would be integrated into LoggingApplicationListener).</p>
 *
 * <h2>How to use</h2>
 * <pre>
 * application.properties:
 *   logging.properties.some.key=Some Value
 *
 * logback.groovy or logback.xml:
 *   ${some.key}
 * </pre>
 *
 * This class is based on https://github.com/lukashinsch/spring-boot-extended-logging-properties.
 */
class ExtendedLoggingPropertiesListener extends LoggingApplicationListener {

    private static final KEY_PREFIX = 'logging.env.'

    /**
     * Ideally this would be run before {@link LoggingApplicationListener} (+11), but after
     * {@link org.springframework.boot.context.config.ConfigFileApplicationListener} (+10)
     * so we could simply insert the properties in between those steps. Unfortunately
     * there is no guaranteed way to do that, so we run afterwards and re-initialize the
     * entire thing via super class.
     */
    int order = HIGHEST_PRECEDENCE + 12


    @Override void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationEnvironmentPreparedEvent) {
            addLoggingProperties(event)
        }
        super.onApplicationEvent(event)
    }

    private void addLoggingProperties(ApplicationEnvironmentPreparedEvent event) {
        new RelaxedPropertyResolver(event.environment)
            .getSubProperties(KEY_PREFIX)
            .each { key, value ->
                System.setProperty(key, value.toString())
            }
    }
}
