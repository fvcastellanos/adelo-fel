package net.cavitos.adelo.fel.controller;

import io.vavr.control.Either;
import net.cavitos.adelo.fel.domain.fel.InvoiceInformation;
import net.cavitos.adelo.fel.domain.views.ErrorResponse;
import net.cavitos.adelo.fel.domain.views.InvoiceGenerationRequest;
import net.cavitos.adelo.fel.domain.views.InvoiceGenerationResponse;

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

@Controller
public class InvoiceController {

    private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceController.class);

    @Autowired
    private InvoiceService invoiceService;
    
    @PostMapping("/invoices")
    public ResponseEntity generateInvoice(@RequestBody InvoiceGenerationRequest invoiceGenerationRequest) {

        LOGGER.info("got invoice generation request");

        // todo: add validator

        Either<List<String>, InvoiceInformation> result = invoiceService.generateElectronicInvoice(invoiceGenerationRequest.getOrderId(),
            invoiceGenerationRequest.getTaxId(), invoiceGenerationRequest.getName(), invoiceGenerationRequest.getEmail());

        if (result.isLeft()) {

            LOGGER.error("errors: {}", result.getLeft());

            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setErrors(result.getLeft());
            return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        InvoiceInformation information = result.get();
        InvoiceGenerationResponse response = new InvoiceGenerationResponse();

        response.setDate(information.getDate());
        response.setDescription(information.getDescription());
        response.setInformation(information.getInformation());
        response.setOrigin(information.getOrigin());

        return new ResponseEntity<InvoiceGenerationResponse>(response, HttpStatus.OK);
    }
}
