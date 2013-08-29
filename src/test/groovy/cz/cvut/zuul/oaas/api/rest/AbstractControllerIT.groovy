package cz.cvut.zuul.oaas.api.rest

import cz.cvut.zuul.oaas.test.AdvicedStandaloneMockMvcBuilder
import cz.cvut.zuul.oaas.test.factories.ObjectFactory
import groovy.json.JsonSlurper
import org.codehaus.jackson.map.ObjectMapper
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import spock.lang.Shared
import spock.lang.Specification

import static org.codehaus.jackson.map.PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
abstract class AbstractControllerIT extends Specification {

    protected static final CONTENT_TYPE_JSON = "application/json;charset=UTF-8"

    @Delegate @Shared
    ObjectFactory factory = new ObjectFactory()

    @Shared MockMvc mockMvc
    @Shared controller
    ResponseWrapper response

    def GET = MockMvcRequestBuilders.&get
    def POST = MockMvcRequestBuilders.&post
    def PUT = MockMvcRequestBuilders.&put
    def DELETE = MockMvcRequestBuilders.&delete


    abstract def initController()
    void setupController(_) {}


    void setupSpec() {
        def messageConverter = new MappingJacksonHttpMessageConverter(
                objectMapper: new ObjectMapper(
                    propertyNamingStrategy: CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES
                )
        )
        controller = initController()
        mockMvc = new AdvicedStandaloneMockMvcBuilder(controller)
                .setControllerAdvices(new CommonExceptionHandler())
                .setMessageConverters(messageConverter)
                .build()
    }

    def setup() {
        setupController(controller)
    }


    def perform(MockHttpServletRequestBuilder requestBuilder) {
        def result = mockMvc.perform(requestBuilder).andReturn()
        response = new ResponseWrapper(result.getResponse());
    }


    static class ResponseWrapper {

        @Delegate
        private final MockHttpServletResponse response

        @Lazy json = new JsonSlurper().parseText(contentAsString)

        ResponseWrapper(MockHttpServletResponse response) {
            this.response = response
        }
    }
}
