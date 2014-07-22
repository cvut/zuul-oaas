/*
 * The MIT License
 *
 * Copyright 2013-2014 Czech Technical University in Prague.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package cz.cvut.zuul.oaas.it.support

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.client.ClientHttpRequest
import org.springframework.http.client.ClientHttpResponse
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.http.converter.FormHttpMessageConverter
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.*

import static org.springframework.http.HttpMethod.GET
import static org.springframework.http.HttpMethod.POST
import static org.springframework.http.HttpStatus.UNAUTHORIZED
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE
import static org.springframework.util.StreamUtils.copyToByteArray

class RestTemplateDSL {

    RestTemplate restTemplate = createDefaultRestTemplate()

    String defaultBaseUri = 'http://localhost'

    Map defaultRequestOpts = [:]

    List<HttpMessageConverter> converters = [
            new FormHttpMessageConverter(),
            new MappingJacksonHttpMessageConverter(),
            new StringHttpMessageConverter()
    ]


    static formatQuery(Map<String, String> query) {
        query.collect { k, v -> "${k}=${v}" }.join('&')
    }


    MyResponseEntity GET(Map<String, ?> opts = [:], String path) {
        execute GET, path, opts
    }

    MyResponseEntity POST(Map<String, ?> opts = [:], String path) {
        execute POST, path, opts
    }


    private createDefaultRestTemplate() {
        def rest = new RestTemplate(new SimpleClientHttpRequestFactory() {
            void prepareConnection(HttpURLConnection conn, String httpMethod) {
                super.prepareConnection(conn, httpMethod)
                conn.instanceFollowRedirects = false
            }
        })
        rest.errorHandler = new ResponseErrorHandler() {
            boolean hasError(ClientHttpResponse response) { false }
            void handleError(ClientHttpResponse response) { }
        }
        rest
    }


    private execute(HttpMethod method, String path, Map<String, ?> opts) {

        // merge with defaults
        opts = defaultRequestOpts + opts

        if (!path.startsWith('http')) {
            path = defaultBaseUri + path
        }
        path += collectQuery(opts)

        def requestCallback = new HttpEntityRequestCallback(parseRequestBody(opts), collectHeaders(opts))
        def responseExtractor = new ConvertibleResponseEntityExtractor()

        restTemplate.execute(path, method, requestCallback, responseExtractor)
    }

    private parseRequestBody(Map<String, ?> opts) {
        def body = opts['body']

        switch (opts['ContentType']?.toString()) {
            case APPLICATION_FORM_URLENCODED_VALUE:
                return body.inject(new LinkedMultiValueMap()) { c, k, v ->
                    c.put(k, v instanceof List ? v : [v]); c
                }
            default:
                opts['body']
        }
    }

    private collectHeaders(Map<String, ?> opts) {

        opts.findAll { k, v ->
            k.chars[0].isUpperCase()
        }.inject(new HttpHeaders()) { headers, k, v ->
            k = k.replaceAll(/\B[A-Z]/) { '-' + it }
            v = v instanceof List ? v : [v as String]
            headers.put(k, v)
            headers
        }
    }

    private collectQuery(Map<String, ?> opts) {
        'query' in opts ? "?${formatQuery(opts['query'])}" : ''
    }


    //////// Inner classes ////////

    private class HttpEntityRequestCallback implements RequestCallback {

        final Object body
        final HttpHeaders headers
        final MediaType contentType


        HttpEntityRequestCallback(Object body, HttpHeaders headers) {
            this.body = body
            this.headers = headers
            this.contentType = headers.getContentType()
        }

        void doWithRequest(ClientHttpRequest request) {

            if (body != null) {
                doWriteBody(request)
            }
            request.headers.putAll(headers)
        }

        void doWriteBody(ClientHttpRequest request) {

            for (converter in converters) {
                if (converter.canWrite(body.getClass(), contentType)) {
                    converter.write(body, contentType, request)
                    return
                }
            }
            // when no converter found
            def msg = "Could not write request: no suitable HttpMessageConverter found " +
                      "for request type [${body.getClass().name}]"
            if (contentType != null) {
                msg += " and content type [${contentType}]"
            }
            throw new RestClientException(msg);
        }
    }


    private static class ConvertibleResponseEntityExtractor implements ResponseExtractor<MyResponseEntity> {

        MyResponseEntity extractData(ClientHttpResponse response) throws IOException {
            def body = response.statusCode == UNAUTHORIZED ? null :
                    new ConvertibleResponseBody(copyToByteArray(response.body))
            new MyResponseEntity(body, response.headers, response.statusCode)
        }
    }
}
