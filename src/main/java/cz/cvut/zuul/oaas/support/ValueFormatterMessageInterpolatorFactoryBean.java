package cz.cvut.zuul.oaas.support;

import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.messageinterpolation.ValueFormatterMessageInterpolator;
import org.hibernate.validator.spi.resourceloading.ResourceBundleLocator;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.MessageSource;
import org.springframework.validation.beanvalidation.MessageSourceResourceBundleLocator;

import javax.validation.MessageInterpolator;

/**
 * {@linkplain FactoryBean} that creates {@link ValueFormatterMessageInterpolator}
 * with underlying {@link ResourceBundleMessageInterpolator} that uses the given
 * {@link MessageSource}.
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public class ValueFormatterMessageInterpolatorFactoryBean implements FactoryBean<MessageInterpolator> {

    private MessageSource messageSource;


    public MessageInterpolator getObject() throws Exception {
        ResourceBundleLocator resourceBundleLocator = new MessageSourceResourceBundleLocator(messageSource);

        return new ValueFormatterMessageInterpolator(
                new ResourceBundleMessageInterpolator(resourceBundleLocator));
    }

    public Class<?> getObjectType() {
        return ValueFormatterMessageInterpolator.class;
    }

    public boolean isSingleton() {
        return true;
    }


    @Required
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
}
