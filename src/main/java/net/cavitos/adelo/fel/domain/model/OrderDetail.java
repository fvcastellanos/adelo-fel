package net.cavitos.adelo.fel.domain.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class OrderDetail {

    private long orderId;
    private long itemId;
    private double unitPrice;
    private double quantity;
    private double discountAmount;
    private double discountTaxable;
    private String itemText;
    private String itemDescription;
}
