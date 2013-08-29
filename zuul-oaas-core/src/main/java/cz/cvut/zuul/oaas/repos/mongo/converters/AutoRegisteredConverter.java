package cz.cvut.zuul.oaas.repos.mongo.converters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.GenericConversionService;

import javax.annotation.PostConstruct;

/**
 * Abstract class for auto-registering {@linkplain Converter converters}.
 *
 * Converters that extends this class are auto-registered to the autowired
 * instance of the {@link ConversionService}.
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@Slf4j
public abstract class AutoRegisteredConverter<S, T> implements Converter<S, T> {

    private @Autowired ConversionService conversionService;


    /**
     * Register itself to the ConversionService.
     * This is called by Spring after dependency injection is done.
     */
    private @PostConstruct void register() {
        if (conversionService instanceof GenericConversionService) {
            log.debug("Registering converter: {}", this.getClass().getName());
            ((GenericConversionService) conversionService).addConverter(this);
        } else {
            log.warn("Could not register converter, ConversionService is not instance of GenericConversionService");
        }
    }

    public ConversionService getConversionService() {
        return conversionService;
    }

    public void setConversionService(ConversionService conversionService) {
        this.conversionService = conversionService;
    }
}
