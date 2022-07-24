package net.cavitos.aldelo.fel.domain.fel;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.cavitos.aldelo.fel.domain.model.OrderDetail;

import java.util.List;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class InvoiceGeneration {

    private long orderId;
    private String taxId;
    private String name;
    private String email;
    private double tipAmount;
    private List<OrderDetail> details;
}
