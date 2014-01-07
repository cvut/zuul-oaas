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
package cz.cvut.zuul.oaas.restapi.controllers;

import com.google.common.collect.Iterables;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.hibernate.validator.method.MethodConstraintViolation;
import org.hibernate.validator.method.MethodConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.io.Serializable;

import static lombok.AccessLevel.PRIVATE;

@Getter
@NoArgsConstructor(access=PRIVATE)
@JsonPropertyOrder({"status", "message", "more_info"})
public class ErrorResponse implements Serializable {

    private static final long serialVersionUID = 2L;

    private int status;

    private String message;

    private String moreInfo;


    private ErrorResponse(int status, String message) {
        this(status, message, null);
    }

    private ErrorResponse(int status, String message, String moreInfo) {
        this.status = status;
        this.message = message;
        this.moreInfo = moreInfo;
    }

    public static ErrorResponse from(HttpStatus status, BindingResult bindingResult) {
        StringBuilder sb = new StringBuilder();

        for (ObjectError error : bindingResult.getAllErrors()) {
            if (sb.length() != 0) {
                sb.append('\n');
            }
            if (error instanceof FieldError) {
                sb.append(((FieldError) error).getField())
                  .append(" ");
            }
            sb.append(error.getDefaultMessage());
        }
        return new ErrorResponse(status.value(), "validation error", sb.toString());
    }

    @SuppressWarnings("deprecation")  // will be changed after JSR-349 release
    public static ErrorResponse from(HttpStatus status, MethodConstraintViolationException exception) {
        StringBuilder sb = new StringBuilder();

        for (MethodConstraintViolation violation : exception.getConstraintViolations()) {
            if (sb.length() != 0) {
                sb.append('\n');
            }
            // TODO FIXME
            if (Iterables.size(violation.getPropertyPath()) > 1) {
                sb.append(Iterables.getLast(violation.getPropertyPath()).getName())
                  .append(" ");
            }
            sb.append(violation.getMessage());
        }
        return new ErrorResponse(status.value(), "validation error", sb.toString());
    }

    public static ErrorResponse from(HttpStatus status, Throwable exception) {
        return new ErrorResponse(status.value(), exception.getLocalizedMessage());
    }
}
