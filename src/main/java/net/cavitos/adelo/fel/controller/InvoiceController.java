package net.cavitos.adelo.fel.controller;

import io.vavr.control.Either;
import net.cavitos.adelo.fel.domain.fel.InvoiceGeneration;
import net.cavitos.adelo.fel.domain.fel.InvoiceInformation;
import net.cavitos.adelo.fel.domain.model.OrderDetail;
import net.cavitos.adelo.fel.domain.views.response.ErrorResponse;
import net.cavitos.adelo.fel.domain.views.request.InvoiceGenerationRequest;
import net.cavitos.adelo.fel.domain.views.response.InvoiceGenerationResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import net.cavitos.adelo.fel.service.InvoiceService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class InvoiceController {

    private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceController.class);

    @Autowired
    private InvoiceService invoiceService;
    
    @PostMapping("/invoices")
    public ResponseEntity generateInvoice(@RequestBody InvoiceGenerationRequest invoiceGenerationRequest) {

        LOGGER.info("got invoice generation request");

        // todo: add validator

        InvoiceGeneration invoiceGeneration = buildInvoiceGeneration(invoiceGenerationRequest);
        Either<List<String>, InvoiceInformation> result = invoiceService.generateElectronicInvoice(invoiceGeneration);

        if (result.isLeft()) {

            LOGGER.error("errors: {}", result.getLeft());
            return buildErrorResponse(result.getLeft());
        }

        return buildSuccessResponse(result.get());
    }

    // --------------------------------------------------------------------------------------------

    private ResponseEntity<ErrorResponse> buildErrorResponse(List<String> errors) {

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrors(errors);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<InvoiceGenerationResponse> buildSuccessResponse(InvoiceInformation information) {

        InvoiceGenerationResponse response = new InvoiceGenerationResponse();

        response.setDate(information.getDate());
        response.setDescription(information.getDescription());
        response.setInformation(information.getInformation());
        response.setOrigin(information.getOrigin());
        response.setUuid(information.getUuid());
        response.setCorrelative(information.getCorrelative());
        response.setNumber(information.getNumber());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private InvoiceGeneration buildInvoiceGeneration(InvoiceGenerationRequest request) {

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
                .email(request.getEmail())
                .name(request.getName())
                .details(details)
                .build();
    }
}
