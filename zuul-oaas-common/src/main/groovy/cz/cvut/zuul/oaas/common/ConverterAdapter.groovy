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
package cz.cvut.zuul.oaas.common

import groovy.transform.CompileStatic
import org.springframework.core.convert.TypeDescriptor
import org.springframework.core.convert.converter.Converter
import org.springframework.core.convert.converter.GenericConverter
import org.springframework.core.convert.converter.GenericConverter.ConvertiblePair
import org.springframework.util.Assert

@CompileStatic
class ConverterAdapter<S, T> implements GenericConverter {

    final Set<ConvertiblePair> convertibleTypes

    private final Converter<S, T> converter
    private final Class<S> sourceClass

    /**
     * @see #createConverter
     */
    ConverterAdapter(Class<S> sourceClass, Class<T> targetClass, Converter<S, T> converter) {
        Assert.notNull(converter, 'Converter must not be null')

        this.converter = converter
        this.convertibleTypes = [ new ConvertiblePair(sourceClass, targetClass) ] as Set
        this.sourceClass = sourceClass
    }

    /**
     * Adapts the given {@link Converter} for the {@GenericConverter} interface
     * with the specified source and target types.
     *
     * @param sourceClass The source type.
     * @param targetClass The target type.
     * @param converter An instance of the {@link Converter} to adapt. Generic
     *        types are not mandatory here.
     * @return An instance of {@link ConverterAdapter} for the given converter.
     */
    static <S, T> ConverterAdapter<S, T> createConverter(Class<S> sourceClass, Class<T> targetClass, Converter<S, T> converter) {
        new ConverterAdapter<S, T>(sourceClass, targetClass, converter)
    }


    /**
     * Converts the source to the target type specified in constructor.
     *
     * @param source The source object to convert (may be null).
     * @param _sourceType This is ignored in this implementation.
     * @param _targetType This is ignored in this implementation.
     * @return The converted object.
     */
    T convert(Object source, TypeDescriptor _sourceType = null, TypeDescriptor _targetType = null) {
        if (source == null) {
            return null
        }
        if (sourceClass.isInstance(source)) {
            return converter.convert((S) source)
        }
        // This shouldn't happen, but just for sure...
        throw new IllegalStateException("Source object isn't instance of <S>")
    }
}
