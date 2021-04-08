package net.cavitos.aldelo.fel.domain.fel;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class ApiInformation {
    
    private String user;
    private String webServiceToken;
    private String signatureAlias;
    private String signatureToken;
    private String salt;
}
