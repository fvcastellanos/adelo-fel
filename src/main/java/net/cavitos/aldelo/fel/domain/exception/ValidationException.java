package net.cavitos.aldelo.fel.domain.exception;

import java.util.List;

import net.cavitos.aldelo.fel.domain.model.error.FieldError;

public class ValidationException extends RuntimeException {

    private final List<FieldError> fieldErrors;

    public ValidationException(final List<FieldError> fieldErrors) {

        this.fieldErrors = fieldErrors;
    }

    public List<FieldError> getFieldErrors() {

        return fieldErrors;
    }    
}
