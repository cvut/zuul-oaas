package cz.cvut.authserver.oauth2.dao.mongo.converters;

import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.GenericConversionService;

/**
 * Abstract class for auto-registering {@linkplain Converter converters}.
 *
 * Converters that extends this class are auto-registered to the autowired
 * instance of the {@link ConversionService}.
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public abstract class AutoRegisteredConverter<S, T> implements Converter<S, T> {

    private static final Logger LOG = LoggerFactory.getLogger(AutoRegisteredConverter.class);

    private @Autowired ConversionService conversionService;


    /**
     * Register itself to the ConversionService.
     * This is called by Spring after dependency injection is done.
     */
    private @PostConstruct void register() {
        if (conversionService instanceof GenericConversionService) {
            LOG.debug("Registering converter: {}", this.getClass().getName());
            ((GenericConversionService) conversionService).addConverter(this);
        } else {
            LOG.warn("Could not register converter, ConversionService is not instance of GenericConversionService");
        }
    }

    public ConversionService getConversionService() {
        return conversionService;
    }

    public void setConversionService(ConversionService conversionService) {
        this.conversionService = conversionService;
    }
}
