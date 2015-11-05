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
package cz.cvut.zuul.oaas.saml.sp.metadata

import groovy.transform.InheritConstructors
import org.opensaml.saml2.metadata.EntityDescriptor
import org.springframework.security.saml.metadata.ExtendedMetadata
import org.springframework.security.saml.metadata.MetadataGenerator

/**
 * An subclass of MetadataGenerator that allows to modify the generated metadata.
 */
@InheritConstructors
class EnhanceableMetadataGenerator extends MetadataGenerator {

    /**
     * A closure that accepts a single argument of type {@link EntityDescriptor}.
     * It's called in {@link #generateMetadata()} before returning the result; to provide
     * an opportunity for customization of the generated metadata.
     */
    Closure metadataEnhancer = Closure.IDENTITY

    /**
     * A closure that accepts a single argument of type {@link ExtendedMetadata}.
     * It's called in {@link #generateExtendedMetadata()} before returning the result;
     * to provide an opportunity for customization of the generated metadata.
     */
    Closure extendedMetadataEnhancer = Closure.IDENTITY


    @Override EntityDescriptor generateMetadata() {
        def metadata = super.generateMetadata()

        metadataEnhancer.call(metadata)
        metadata
    }

    @Override ExtendedMetadata generateExtendedMetadata() {
        def metadata = super.generateExtendedMetadata()

        extendedMetadataEnhancer.call(metadata)
        metadata
    }
}
