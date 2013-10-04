package cz.cvut.zuul.oaas.api.rest;

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
 *
 * @author Jakub Jirutka <jakub@jirutka.cz>
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
