package net.cavitos.aldelo.fel.domain.exception;

import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpStatus;

public class BusinessException extends RuntimeException {
    
    private final String message;
    private final List<String> details;
    private final String field;
    private final HttpStatus httpStatus;

    public BusinessException() {

        super("Internal Server Error");
        this.message = "Internal Server Error";
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        this.field = "";
        this.details = Collections.emptyList();
    }

    public BusinessException(final String message) {

        super(message);
        this.message = message;
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        this.field = "";
        this.details = Collections.emptyList();
    }

    public BusinessException(final HttpStatus httpStatus, final String message) {

        super(message);
        this.message = message;
        this.httpStatus = httpStatus;
        this.field = "";
        this.details = Collections.emptyList();
    }

    public BusinessException(final HttpStatus httpStatus, final String message, final List<String> details) {

        super(message);
        this.message = message;
        this.httpStatus = httpStatus;
        this.field = "";
        this.details = details;
    }

    public BusinessException(final HttpStatus httpStatus, 
                             final String message, 
                             final String field) {

        super(message);
        this.message = message;
        this.httpStatus = httpStatus;
        this.field = field;
        this.details = Collections.emptyList();
    }


    public String getMessage() {

        return message;
    }

    public String getField() {

        return field;
    }

    public HttpStatus getHttpStatus() {

        return httpStatus;
    }        

    public List<String> getDetails() {

        return details;
    }
}
