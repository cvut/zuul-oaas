package cz.cvut.zuul.oaas.restapi.config

import org.codehaus.jackson.map.ObjectMapper
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter

import static org.codehaus.jackson.map.PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES

@Configuration
@EnableWebMvc
class RestContextConfig extends WebMvcConfigurerAdapter {

    void configureMessageConverters(List converters) {
        converters << new MappingJacksonHttpMessageConverter (
            objectMapper: new ObjectMapper (
                propertyNamingStrategy: CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES
            )
        )
    }
}
