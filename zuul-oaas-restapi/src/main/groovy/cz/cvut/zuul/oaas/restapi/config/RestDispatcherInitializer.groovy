package cz.cvut.zuul.oaas.restapi.config

import org.springframework.core.annotation.Order
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer

@Order(1)
class RestDispatcherInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    String servletName = 'api'

    Class[] rootConfigClasses = null //do not initialize root context here

    Class[] servletConfigClasses = [ RestContextConfig, RestControllersConfig ]

    String[] servletMappings = [ '/api/*' ]
}
