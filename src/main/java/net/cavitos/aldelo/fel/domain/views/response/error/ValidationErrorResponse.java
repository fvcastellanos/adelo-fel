package net.cavitos.aldelo.fel.domain.views.response.error;

import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.cavitos.aldelo.fel.domain.model.error.FieldError;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ValidationErrorResponse {
    
    private String message;
    private List<FieldError> errors;    
}
