package cz.cvut.authserver.oauth2.api.models;

import com.google.common.collect.Iterables;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.hibernate.validator.method.MethodConstraintViolation;
import org.hibernate.validator.method.MethodConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@JsonSerialize(include = Inclusion.ALWAYS)
@JsonPropertyOrder({"status", "messages"})
public class ErrorResponse {

    private int status;
    private List<String> messages;


    private ErrorResponse() {
    }

    private ErrorResponse(int status, List<String> messages) {
        this.status = status;
        this.messages = messages;
    }

    public static ErrorResponse from(HttpStatus status, BindingResult bindingResult) {
        List<String> messages = new ArrayList<>(2);

        for (ObjectError error : bindingResult.getAllErrors()) {
            String msg = "";

            if (error instanceof FieldError) {
                msg = ((FieldError) error).getField() + " ";
            }
            messages.add(msg += error.getDefaultMessage());
        }
        return new ErrorResponse(status.value(), messages);
    }

    @SuppressWarnings("deprecation")  // will be changed after JSR-349 release
    public static ErrorResponse from(HttpStatus status, MethodConstraintViolationException exception) {
        List<String> messages = new ArrayList<>(2);

        for (MethodConstraintViolation violation : exception.getConstraintViolations()) {
            String msg = "";

            // TODO FIXME
            if (Iterables.size(violation.getPropertyPath()) > 1) {
                msg = Iterables.getLast(violation.getPropertyPath()).getName() + " ";
            }
            messages.add(msg += violation.getMessage());
        }
        return new ErrorResponse(status.value(), messages);
    }


    public int getStatus() {
        return status;
    }

    public List<String> getMessages() {
        return messages;
    }

}
