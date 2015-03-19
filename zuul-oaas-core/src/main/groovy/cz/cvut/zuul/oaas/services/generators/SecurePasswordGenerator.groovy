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
package cz.cvut.zuul.oaas.services.generators

import org.apache.commons.lang3.RandomStringUtils
import org.springframework.security.crypto.keygen.StringKeyGenerator

import java.security.SecureRandom

class SecurePasswordGenerator implements StringKeyGenerator {

    final int length

    private final Random randomGenerator


    /**
     * @param length The length of random password to generate (default is 32).
     * @param randomGenerator A source of randomness (default is {@link SecureRandom}).
     */
    SecurePasswordGenerator(int length = 32, Random randomGenerator = new SecureRandom()) {
        this.length = length
        this.randomGenerator = randomGenerator
    }

    String generateKey() {
        RandomStringUtils.random(length, 0, 0, true, true, null, randomGenerator)
    }
}
