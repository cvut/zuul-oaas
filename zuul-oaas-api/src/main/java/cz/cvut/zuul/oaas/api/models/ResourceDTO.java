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

import cz.cvut.zuul.oaas.api.validators.ValidURI;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

import static javax.validation.constraints.Pattern.Flag.CASE_INSENSITIVE;

@Data
public class ResourceDTO implements Serializable {

    private String resourceId;

    private Auth auth;

    @NotEmpty @Size(max=256)
    @ValidURI(scheme={"https", "http"})
    private String baseUrl;

    @Size(max=256)
    private String description;

    @NotEmpty @Size(max=256)
    private String name;

    @Size(max=256)
    private String version;

    @NotEmpty
    @Pattern(regexp="(public|hidden)", flags=CASE_INSENSITIVE)
    private String visibility;



    @Data
    public static class Auth implements Serializable {

        private List<Scope> scopes;
    }


    @Data
    public static class Scope implements Serializable {

        @Size(max=256)
        private String name;

        @Size(max=256)
        private String description;

        private boolean secured = false;
    }
}
