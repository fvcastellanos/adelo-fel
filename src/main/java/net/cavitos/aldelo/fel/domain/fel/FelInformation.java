package net.cavitos.aldelo.fel.domain.fel;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class FelInformation {
    
    private String currencyCode;
    private String documentType;
    private String exportation;
    private Integer accessNumber;
    private String person;   
    private Generator generator;
}
