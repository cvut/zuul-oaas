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

import cz.cvut.zuul.oaas.api.exceptions.ConflictException;
import cz.cvut.zuul.oaas.api.exceptions.NotFoundException;
import org.hibernate.validator.method.MethodConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.BadClientCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpStatus.*;

/**
 * Exception handlers shared across all controllers.
 */
@ControllerAdvice
public class CommonExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BadClientCredentialsException.class)
    ResponseEntity<Void> handleBadClientCredentialsException() {
        return new ResponseEntity<>(UNAUTHORIZED);
    }

    @ResponseBody
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MethodConstraintViolationException.class)
    @SuppressWarnings("deprecation") // will be changed after JSR-349 release
    ErrorResponse handleViolationException(MethodConstraintViolationException ex) {
        return ErrorResponse.from(BAD_REQUEST, ex);
    }

    @ResponseBody
    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    ErrorResponse handleNotFoundException(NotFoundException ex) {
        return ErrorResponse.from(NOT_FOUND, ex);
    }

    @ResponseBody
    @ResponseStatus(CONFLICT)
    @ExceptionHandler(ConflictException.class)
    ErrorResponse handleConflictException(ConflictException ex) {
        return ErrorResponse.from(CONFLICT, ex);
    }
}
