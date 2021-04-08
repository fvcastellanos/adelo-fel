package net.cavitos.aldelo.fel.domain.views.request;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class OrderDetail {

    private double unitPrice;
    private double quantity;
    private double discountAmount;
    private String itemText;
}
