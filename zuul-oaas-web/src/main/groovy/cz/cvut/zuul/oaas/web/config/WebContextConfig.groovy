package cz.cvut.zuul.oaas.web.config

import cz.cvut.zuul.oaas.common.config.ConfigurationSupport
import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.config.PlaceholderConfigurerSupport
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
import org.thymeleaf.spring3.SpringTemplateEngine
import org.thymeleaf.spring3.view.ThymeleafViewResolver
import org.thymeleaf.templateresolver.ServletContextTemplateResolver

import javax.inject.Inject

@Configuration
@EnableWebMvc
@Mixin(ConfigurationSupport)
class WebContextConfig extends WebMvcConfigurerAdapter {

    // Initialize mixed in ConfigurationSupport
    @Inject initSupport(ApplicationContext ctx) { _initSupport(ctx) }

    /**
     * {@code BeanFactoryPostProcessor} that inherits existing property
     * placeholder resolver from the parent context in this context.
     */
    @Bean static BeanFactoryPostProcessor parentPlaceholderConfigurerPostProcessor() {
        new BeanFactoryPostProcessor() {
            void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
                beanFactory.getBean(PlaceholderConfigurerSupport).postProcessBeanFactory(beanFactory)
            }
        }
    }

    void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable()
    }


    @Bean thymeleafViewResolver() {
        new ThymeleafViewResolver (
            templateEngine:     templateEngine(),
            contentType:        'text/html;charset=UTF-8'
        )
    }

    @Bean templateEngine() {
        new SpringTemplateEngine (
            templateResolver:   templateResolver(),
            messageSource:      messageSource()
        )
    }

    @Bean templateResolver() {
        new ServletContextTemplateResolver (
            prefix:             '/WEB-INF/templates/',
            suffix:             '.html',
            characterEncoding:  'utf-8',
            templateMode:       'HTML5',
            cacheable:          ! profileDev
        )
    }

    @Bean messageSource() {
        new ReloadableResourceBundleMessageSource (
            basename:           'classpath:/config/web/i18n/web-messages',
            defaultEncoding:    'utf-8',
            cacheSeconds:       profileDev ? 5 : 120
        )
    }
}
