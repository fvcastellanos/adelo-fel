package net.cavitos.aldelo.fel.domain.views.request;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class InvoiceGenerationRequest {

    private long orderId;
    private String taxId;
    private String name;
    private String email;
    private double tipAmount;
    private List<OrderDetail> details;
}
