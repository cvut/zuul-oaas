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
package cz.cvut.zuul.oaas.restapi.test;

import org.springframework.beans.factory.support.StaticListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.handler.HandlerExceptionResolverComposite;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

public class AdvicedStandaloneMockMvcBuilder extends StandaloneMockMvcBuilder {

    private Object[] advices = {};

    public AdvicedStandaloneMockMvcBuilder(Object... controllers) {
        super(controllers);
    }

    public AdvicedStandaloneMockMvcBuilder setControllerAdvices(Object... advices) {
        this.advices = advices;
        return this;
    }

    @Override
    protected WebApplicationContext initWebAppContext() {
        WebApplicationContext cxt = super.initWebAppContext();

        StaticListableBeanFactory beanFactory = (StaticListableBeanFactory) cxt.getAutowireCapableBeanFactory();

        for (Object advice : advices) {
            beanFactory.addBean(advice.getClass().getSimpleName(), advice);
        }
        refreshExceptionResolvers(cxt);

        return cxt;
    }

    protected void refreshExceptionResolvers(ApplicationContext cxt) {
        HandlerExceptionResolverComposite composite
                = cxt.getAutowireCapableBeanFactory().getBean(HandlerExceptionResolverComposite.class);

        for (HandlerExceptionResolver resolver : composite.getExceptionResolvers()) {
            if (resolver instanceof ExceptionHandlerExceptionResolver) {
                ExceptionHandlerExceptionResolver casted = (ExceptionHandlerExceptionResolver) resolver;
                casted.setApplicationContext(cxt);
                casted.afterPropertiesSet();
            }
        }
    }
}
