/*
 * The MIT License
 *
 * Copyright 2013-2015 Czech Technical University in Prague.
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
package cz.cvut.zuul.oaas.support

import org.springframework.boot.env.PropertySourceLoader
import org.springframework.core.env.PropertiesPropertySource
import org.springframework.core.env.PropertySource
import org.springframework.core.io.Resource
import org.springframework.core.io.support.EncodedResource
import org.springframework.core.io.support.PropertiesLoaderUtils

/**
 * Strategy to load UTF-8 encoded '.properties' files into a {@link PropertySource}.
 *
 * @see org.springframework.boot.env.PropertiesPropertySourceLoader
 */
class UnicodePropertiesPropertySourceLoader implements PropertySourceLoader {

    String[] fileExtensions = ['properties']


    PropertySource<?> load(String name, Resource resource, String profile) {
        if (!profile) {
            def props = PropertiesLoaderUtils.loadProperties(new EncodedResource(resource, 'UTF-8'))

            if (!props.empty) {
                return new PropertiesPropertySource(name, props)
            }
        }
        null
    }
}
