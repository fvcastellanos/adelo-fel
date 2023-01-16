package net.cavitos.aldelo.fel.domain.views.request;

import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class GenerationRequest {
    
    @NotNull
    private List<InvoiceGenerationRequest> invoices;
}
