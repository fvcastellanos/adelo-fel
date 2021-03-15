package net.cavitos.adelo.fel.domain.views;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class InvoiceGenerationRequest {

    private long orderId;
    private String taxId;
    private String name;
    private String email;
}
