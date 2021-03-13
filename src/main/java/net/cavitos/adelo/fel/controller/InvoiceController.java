package net.cavitos.adelo.fel.controller;

import io.vavr.control.Either;
import net.cavitos.adelo.fel.domain.fel.InvoiceInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import net.cavitos.adelo.fel.service.InvoiceService;

import java.util.List;

@Controller
public class InvoiceController {

    private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceController.class);

    @Autowired
    private InvoiceService invoiceService;
    
    @GetMapping("/invoices")
    public String generateInvoice() {

        LOGGER.info("got invoice generation request");

        Either<List<String>, InvoiceInformation> result = invoiceService.generateElectronicInvoice(1, "123123",
                "Juan Penas", "adelo@mailnator.com");

        if (result.isLeft()) {

            LOGGER.error("errors: {}", result.getLeft());
            return "ERROR";
        }

        return "OK";
    }
}
