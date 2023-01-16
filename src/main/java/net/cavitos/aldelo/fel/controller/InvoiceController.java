package net.cavitos.aldelo.fel.controller;

import net.cavitos.aldelo.fel.domain.fel.InvoiceGeneration;
import net.cavitos.aldelo.fel.domain.fel.InvoiceInformation;
import net.cavitos.aldelo.fel.domain.fel.InvoiceType;
import net.cavitos.aldelo.fel.domain.model.OrderDetail;
import net.cavitos.aldelo.fel.domain.views.response.GenerationResponse;
import net.cavitos.aldelo.fel.domain.views.request.GenerationRequest;
import net.cavitos.aldelo.fel.domain.views.request.InvoiceGenerationRequest;
import net.cavitos.aldelo.fel.domain.views.response.InvoiceGenerationResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import net.cavitos.aldelo.fel.service.InvoiceService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

@Controller
public class InvoiceController {

    private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceController.class);

    @Autowired
    private InvoiceService invoiceService;
    
    @PostMapping("/invoices")
    public ResponseEntity<GenerationResponse> generateInvoice(@Valid @RequestBody final GenerationRequest generationRequest) {

        LOGGER.info("got invoice generation request");

        final List<InvoiceGenerationResponse> invoiceGenerationResponses = new ArrayList<>();

        generationRequest.getInvoices().forEach(request -> {

            LOGGER.info("invoice generation for invoice_type={}", request.getType());

            final InvoiceType invoiceType = InvoiceType.of(request.getType());

            final InvoiceGeneration invoiceGeneration = buildInvoiceGeneration(request);
            final InvoiceInformation invoiceInformation = invoiceService.generateElectronicInvoice(invoiceGeneration, invoiceType);

            LOGGER.info("invoice generate for invoice_type={} and order_id={}", request.getType(), request.getOrderId());

            invoiceGenerationResponses.add(buildInvoiceGenerationResponse(invoiceInformation));
        });

        return buildGenerationResponse(invoiceGenerationResponses);
    }

    // --------------------------------------------------------------------------------------------

    private ResponseEntity<GenerationResponse> buildGenerationResponse(final List<InvoiceGenerationResponse> responses) {

        final GenerationResponse generationResponse = new GenerationResponse();
        generationResponse.setInvoices(responses);

        return new ResponseEntity<GenerationResponse>(generationResponse, HttpStatus.OK);
    }

    private InvoiceGenerationResponse buildInvoiceGenerationResponse(final InvoiceInformation information) {

        final InvoiceGenerationResponse response = new InvoiceGenerationResponse();

        response.setDate(information.getDate());
        response.setDescription(information.getDescription());
        response.setInformation(information.getInformation());
        response.setOrigin(information.getOrigin());
        response.setUuid(information.getUuid());
        response.setCorrelative(information.getCorrelative());
        response.setNumber(information.getNumber());
        response.setType(InvoiceType.of(information.getType()));

        return response;
    }

    private InvoiceGeneration buildInvoiceGeneration(final InvoiceGenerationRequest request) {

        List<OrderDetail> details = request.getDetails().stream()
                .map(orderDetail -> OrderDetail.builder()
                            .itemText(orderDetail.getItemText())
                            .unitPrice(orderDetail.getUnitPrice())
                            .quantity(orderDetail.getQuantity())
                            .discountAmount(orderDetail.getDiscountAmount())
                            .build())
                .collect(Collectors.toList());

        return InvoiceGeneration.builder()
                .orderId(request.getOrderId())
                .taxId(request.getTaxId())
                .taxIdType(request.getTaxIdType())
                .email(request.getEmail())
                .name(request.getName())
                .tipAmount(request.getTipAmount())
                .details(details)
                .build();
    }
}
