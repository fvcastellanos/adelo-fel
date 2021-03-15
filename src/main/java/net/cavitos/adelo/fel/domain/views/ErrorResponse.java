package net.cavitos.adelo.fel.domain.views;

import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ErrorResponse {
    
    private List<String> errors;
}
