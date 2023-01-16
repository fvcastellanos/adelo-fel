package net.cavitos.aldelo.fel.domain.views.request;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class InvoiceGenerationRequest {

    @NotNull
    @NotBlank
    @Size(max = 30)
    private String type;

    @NotNull
    private long orderId;

    @NotBlank
    @Size(max = 50)
    private String taxId;

    private String taxIdType;

    @Size(max = 150)
    private String name;

    @Size(max = 350)
    private String email;

    private double tipAmount;

    @NotNull
    private List<OrderDetail> details;
}
