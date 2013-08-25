package cz.cvut.zuul.oaas.api.models;

import com.google.common.collect.Iterables;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.hibernate.validator.method.MethodConstraintViolation;
import org.hibernate.validator.method.MethodConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.io.Serializable;

/**
 * @author Jakub Jirutka <jakub@jirutka.cz>
 */
@JsonSerialize(include = Inclusion.ALWAYS)
@JsonPropertyOrder({"status", "message", "more_info"})
public class ErrorResponse implements Serializable {

    private static final long serialVersionUID = 2L;

    private int status;

    private String message;

    @JsonProperty("more_info")
    private String moreInfo;


    private ErrorResponse() {
    }

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


    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getMoreInfo() {
        return moreInfo;
    }
}
