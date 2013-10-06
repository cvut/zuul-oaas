package cz.cvut.zuul.oaas.api.rest

import cz.cvut.zuul.oaas.test.AdvicedStandaloneMockMvcBuilder
import cz.cvut.zuul.oaas.test.factories.ObjectFactory
import groovy.json.JsonSlurper
import org.codehaus.jackson.map.ObjectMapper
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.RequestPostProcessor
import org.springframework.web.bind.annotation.RequestMapping
import spock.lang.Shared
import spock.lang.Specification

import static org.codehaus.jackson.map.PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES
import static org.springframework.http.MediaType.APPLICATION_JSON

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

    def baseUri

    static GET = MockMvcRequestBuilders.&get
    static POST = MockMvcRequestBuilders.&post
    static PUT = MockMvcRequestBuilders.&put
    static DELETE = MockMvcRequestBuilders.&delete


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
                .defaultRequest(GET('/')
                    .accept( APPLICATION_JSON )
                    .contentType( APPLICATION_JSON) )
                .build()
    }

    def setup() {
        setupController(controller)

        baseUri = getBaseUri() ?: determineBaseUri()
        assert baseUri, 'baseUri was not found, must be specified explicitly'
    }


    def perform(MockHttpServletRequestBuilder requestBuilder) {
        requestBuilder.with(new RequestPostProcessor() {
            MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.requestURI = "${getBaseUri()}/${request.requestURI}"
                return request
            }
        })
        def result = mockMvc.perform(requestBuilder).andReturn()
        response = new ResponseWrapper(result.getResponse())
    }

    private determineBaseUri() {
        def requestMapping = controller.class.getAnnotation(RequestMapping)
        if (requestMapping) {
            def uri = requestMapping.value()[0]
            return uri.endsWith('/') ? uri[0..-2] : uri
        }
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
