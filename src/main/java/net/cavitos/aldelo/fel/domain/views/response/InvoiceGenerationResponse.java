package net.cavitos.aldelo.fel.domain.views.response;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.cavitos.aldelo.fel.domain.fel.InvoiceType;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class InvoiceGenerationResponse {

    private InvoiceType type;
    private String date;
    private String origin;
    private String information;
    private String description;
    private String uuid;
    private String correlative;
    private String number;
}
