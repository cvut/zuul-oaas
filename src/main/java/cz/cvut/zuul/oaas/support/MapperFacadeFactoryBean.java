package cz.cvut.zuul.oaas.support;

import ma.glasnost.orika.Converter;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.ConverterFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.beans.factory.FactoryBean;

import java.util.Collection;
import java.util.Collections;

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
public class MapperFacadeFactoryBean implements FactoryBean<MapperFacade> {

    private Collection<Converter> converters = Collections.emptyList();


    public MapperFacade getObject() throws Exception {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        ConverterFactory converterFactory = mapperFactory.getConverterFactory();

        for (Converter converter : converters) {
            converterFactory.registerConverter(converter);
        }

        return mapperFactory.getMapperFacade();
    }

    public Class<?> getObjectType() {
        return MapperFacade.class;
    }

    public boolean isSingleton() {
        return true;
    }


    public Collection<Converter> getConverters() {
        return converters;
    }

    public void setConverters(Collection<Converter> converters) {
        this.converters = converters;
    }
}
