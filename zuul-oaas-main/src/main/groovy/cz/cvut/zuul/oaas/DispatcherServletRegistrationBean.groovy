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
package cz.cvut.zuul.oaas

import org.springframework.boot.context.embedded.ServletRegistrationBean
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext
import org.springframework.web.servlet.DispatcherServlet

import javax.servlet.Servlet
import javax.servlet.ServletContext

/**
 * A {@link org.springframework.boot.context.embedded.ServletContextInitializer ServletContextInitializer}
 * to register a {@link DispatcherServlet} configured with annotated classes,
 * e.g. Spring's {@link org.springframework.context.annotation.Configuration @Configuration}
 * classes.
 *
 * <p>At least one {@linkplain #setConfigClasses(Class[]) configuration class}
 * must be specified before calling {@link #onStartup}. URL mapping can be
 * configured used {@link #setUrlMappings} or omitted when mapping to '/*'. The
 * servlet name will be deduced if not specified.</p>
 *
 * @see DispatcherServlet
 * @see AnnotationConfigWebApplicationContext
 * @see org.springframework.boot.context.embedded.ServletContextInitializer
 */
class DispatcherServletRegistrationBean extends ServletRegistrationBean {

    /**
     * Specify {@link org.springframework.context.annotation.Configuration @Configuration}
     * and/or {@link org.springframework.stereotype.Component @Component} classes to be
     * provided to the {@linkplain AnnotationConfigWebApplicationContext web application
     * context}.
     */
    Class<?>[] configClasses


    @Override
    void onStartup(ServletContext servletContext) {

        def cxt = new AnnotationConfigWebApplicationContext()
        cxt.register(configClasses)

        super.setServlet(new DispatcherServlet(cxt))
        super.addInitParameter('throwExceptionIfNoHandlerFound', 'true')

        super.onStartup(servletContext)
    }

    /**
     * Not implemented in this class uses {@link DispatcherServlet}.
     */
    @Override
    void setServlet(Servlet servlet) {
    }
}
