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
package cz.cvut.zuul.oaas.api.models;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Data
public class TokenDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Date expiration;
    private ClientAuthentication clientAuthentication;
    private Set<String> scope;
    private String tokenType;
    private String tokenValue;
    private UserAuthentication userAuthentication;


    @Data
    public static class ClientAuthentication implements Serializable {

        private static final long serialVersionUID = 1L;

        private String clientId;
        private Boolean clientLocked;
        private String productName;
        private Set<String> scope;
        private String redirectUri;
        private Set<String> resourceIds;
    }


    @Data
    public static class UserAuthentication implements Serializable {

        private static final long serialVersionUID = 1L;

        private String username;
        private String email;
        private String firstName;
        private String lastName;
    }
}
