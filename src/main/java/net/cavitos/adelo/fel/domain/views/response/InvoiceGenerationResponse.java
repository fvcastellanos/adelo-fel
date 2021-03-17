package net.cavitos.adelo.fel.domain.views.response;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class InvoiceGenerationResponse {

    private String date;
    private String origin;
    private String information;
    private String description;
    private String uuid;
    private String correlative;
    private String number;
}
