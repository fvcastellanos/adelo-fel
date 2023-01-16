package net.cavitos.aldelo.fel.domain.views.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class OrderDetail {

    @NotNull
    private double unitPrice;

    @NotNull
    private double quantity;

    private double discountAmount;

    @NotBlank
    @Size(max = 150)
    private String itemText;
}
