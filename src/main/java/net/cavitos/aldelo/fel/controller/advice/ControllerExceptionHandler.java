package net.cavitos.aldelo.fel.controller.advice;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import net.cavitos.aldelo.fel.domain.exception.BusinessException;
import net.cavitos.aldelo.fel.domain.exception.ValidationException;
import net.cavitos.aldelo.fel.domain.model.error.FieldError;
import net.cavitos.aldelo.fel.domain.views.response.ErrorResponse;
import net.cavitos.aldelo.fel.domain.views.response.error.ValidationErrorResponse;

@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerExceptionHandler.class);  

    @ExceptionHandler
    public ResponseEntity<Object> handleFallBackException(Exception exception, WebRequest request) {

        LOGGER.error("unable to process request - ", exception);

        final ErrorResponse error = buildErrorResponse(Collections.singletonList("Unable to process request / service unavailable"));

        return handleExceptionInternal(exception, error, buildHttpHeaders(), 
            HttpStatus.INTERNAL_SERVER_ERROR, request);
    }    

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Object> handleBusinessException(BusinessException exception, WebRequest request) {

        LOGGER.error("unable to process request - ", exception);

        final ErrorResponse error = buildErrorResponse(Collections.singletonList(exception.getMessage()));
        
        return handleExceptionInternal(exception, error, buildHttpHeaders(),
            exception.getHttpStatus(), request);        
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Object> handleRequestValidationException(ValidationException exception, WebRequest request) {

        LOGGER.error("unable to process request because a validation exception - ", exception);

        final ValidationErrorResponse error = buildValidationErrorResponse(exception.getFieldErrors());
        return handleExceptionInternal(exception, error, buildHttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    // ------------------------------------------------------------------------------------------------------

    private ErrorResponse buildErrorResponse(final List<String> errors) {

        final ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrors(errors);

        return errorResponse;
    }

    private ValidationErrorResponse buildValidationErrorResponse(List<FieldError> errors) {

        final ValidationErrorResponse error = new ValidationErrorResponse();
        error.setErrors(errors);
        error.setMessage("Request validation failed");

        return error;
    }
        
    private HttpHeaders buildHttpHeaders() {

        return new HttpHeaders();
    }        
}
