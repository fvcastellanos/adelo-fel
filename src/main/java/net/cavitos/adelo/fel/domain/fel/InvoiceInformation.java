package net.cavitos.adelo.fel.domain.fel;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class InvoiceInformation {

    private String uuid;
    private String correlative;
    private String number;
    private String information;
    private String description;
    private String date;
    private String origin;
}
