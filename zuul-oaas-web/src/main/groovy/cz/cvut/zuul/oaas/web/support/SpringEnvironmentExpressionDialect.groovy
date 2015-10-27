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
package cz.cvut.zuul.oaas.web.support

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.springframework.core.env.Environment
import org.thymeleaf.context.IProcessingContext
import org.thymeleaf.dialect.AbstractDialect
import org.thymeleaf.dialect.IExpressionEnhancingDialect

/**
 * Enhancing dialect meant for use with {@link org.thymeleaf.spring4.dialect.SpringStandardDialect
 * SpringStandardDialect} that provide access to the application's {@link Environment}.
 *
 * <p>Examples:
 * <pre>
 * ${#env['some.property']}
 * ${#env.getProperty('some.property', 'defaultValue')}
 * ${#env.activeProfiles}
 * ${#env.acceptsProfiles('dev')}
 * </pre></p>
 */
class SpringEnvironmentExpressionDialect extends AbstractDialect implements IExpressionEnhancingDialect {

    private final environmentAdapter

    String prefix = null


    SpringEnvironmentExpressionDialect(Environment environment) {
        this.environmentAdapter = new EnvironmentMapAdapter(environment)
    }

    Map<String, Object> getAdditionalExpressionObjects(IProcessingContext processingContext) {
        [env: environmentAdapter]
    }


    @ToString(includes = 'environment')
    @EqualsAndHashCode(includes = 'environment')
    protected static class EnvironmentMapAdapter
        extends AbstractMap<String, String> implements Environment {

        @Delegate
        private final Environment environment

        EnvironmentMapAdapter(Environment environment) {
            this.environment = environment
        }

        @Override boolean isEmpty() {
            false
        }

        @Override boolean containsValue(value) {
            false
        }

        @Override boolean containsKey(key) {
            environment.containsProperty(key?.toString())
        }

        @Override String get(key) {
            environment.getProperty(key?.toString())
        }

        @Override Set keySet() {
            throw new UnsupportedOperationException()
        }

        @Override Set entrySet() {
            throw new UnsupportedOperationException()
        }

        @Override Collection values() {
            throw new UnsupportedOperationException()
        }
    }
}
