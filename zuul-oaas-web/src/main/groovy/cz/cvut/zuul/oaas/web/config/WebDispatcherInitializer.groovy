package cz.cvut.zuul.oaas.web.config

import org.springframework.core.annotation.Order
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer

@Order(2)
class WebDispatcherInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    String servletName = 'web'

    Class[] rootConfigClasses = null //do not initialize root context here

    Class[] servletConfigClasses = [ WebContextConfig, WebControllersConfig ]

    String[] servletMappings = [ '/' ]
}
