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
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.FromString

@CompileStatic
class Looper<T> {

    private final Closure<T> block


    private Looper(Closure<T> block) {
        this.block = vargsClosure(block)
    }

    /**
     * @param block The code to run in loop.
     */
    static <T> Looper<T> loop(@ClosureParams(value=FromString, options=['T', 'T,int']) Closure<T> block) {
        new Looper<T>(block)
    }

    /**
     * Call the block again and again until the given closure yields <tt>false</tt>.
     *
     * @param closure A zero, one or two arguments closure that returns <tt>false</tt>
     *        to call the block again, or <tt>true</tt> to stop.
     *        The first argument is result of the last block invocation.
     *        The second argument is number of already done iterations.
     * @return A result of the last invocation of the block.
     */
    T until(@ClosureParams(value=FromString, options=['T', 'T,int']) Closure<Boolean> closure) {

        closure = vargsClosure(closure)
        def i = 0
        def result = block(null, i)

        while (!closure(result, i++)) {
            result = block(result, i)
        }
        result
    }

    private Closure vargsClosure(Closure closure) {
        switch (closure.maximumNumberOfParameters) {
            case 0  : return { ...vargs -> closure() }
            case 1  : return { ...vargs -> closure(vargs[0]) }
            default : return { ...vargs -> closure.call(vargs.toList()) }
        }
    }
}
