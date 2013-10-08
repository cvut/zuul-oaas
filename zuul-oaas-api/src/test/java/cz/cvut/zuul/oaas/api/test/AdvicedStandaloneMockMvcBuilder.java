package cz.cvut.zuul.oaas.api.test;

import org.springframework.beans.factory.support.StaticListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.handler.HandlerExceptionResolverComposite;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
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
    protected void initWebAppContext(WebApplicationContext cxt) {
        super.initWebAppContext(cxt);

        StaticListableBeanFactory beanFactory = (StaticListableBeanFactory) cxt.getAutowireCapableBeanFactory();

        for (Object advice : advices) {
            beanFactory.addBean(advice.getClass().getSimpleName(), advice);
        }
        refreshExceptionResolvers(cxt);
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
