package net.cavitos.aldelo.fel.domain.fel;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class Phrase {
    
    private Integer type;
    private Integer scenario;
    private String resolutionNumber;
    private String resolutionDate;
}
