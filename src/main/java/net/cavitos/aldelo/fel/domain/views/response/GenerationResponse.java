package net.cavitos.aldelo.fel.domain.views.response;

import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class GenerationResponse {
    
    private List<InvoiceGenerationResponse> invoices;
}
