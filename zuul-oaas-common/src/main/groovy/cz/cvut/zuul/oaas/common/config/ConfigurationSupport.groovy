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
package cz.cvut.zuul.oaas.common.config

import cz.cvut.zuul.oaas.common.ext.StringAsBoolean
import groovy.transform.CompileStatic
import org.springframework.beans.factory.FactoryBean
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer
import org.springframework.core.env.Environment
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader

@CompileStatic
trait ConfigurationSupport {

    @Autowired ResourceLoader resourceLoader
    @Autowired Environment env

    private ConfigurableListableBeanFactory beanFactory


    /**
     * This method may be overrided by implementing class to provide local
     * properties.
     *
     * @return instance of {@link Properties}.
     */
    def getLocalProperties() {
        new Properties()
    }

    @Autowired
    void __initialize(ConfigurableListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory
        loadProperties()
    }


    Resource resource(String location) {
        resourceLoader.getResource(location)
    }

    ClassPathResource classpath(String path) {
        new ClassPathResource(path)
    }

    def <T> T initialize(FactoryBean<T> factoryBean) throws Exception {
        if (factoryBean instanceof InitializingBean) {
            factoryBean.afterPropertiesSet()
        }
        factoryBean.getObject()
    }

    String p(String propertyKey) {

        def value = env.containsProperty(propertyKey) \
            ? env.getProperty(propertyKey)
                : beanFactory.resolveEmbeddedValue(String.format('${%s}', propertyKey))

        value.metaClass.mixin(StringAsBoolean)

        return value
    }

    boolean isActive(String... profile) {
        env.acceptsProfiles(profile)
    }

    boolean isProfileDev() {
        isActive('dev')
    }


    private loadProperties() {
        def props = new Properties()

        if (localProperties instanceof Map) {
            props.putAll(localProperties as Map)
        } else {
            throw new IllegalArgumentException('localProperties() must return Map or Properties')
        }

        if (! props.isEmpty()) {
            def conf = new PropertyPlaceholderConfigurer()
            conf.properties = props
            conf.postProcessBeanFactory(beanFactory)
        }
    }
}
