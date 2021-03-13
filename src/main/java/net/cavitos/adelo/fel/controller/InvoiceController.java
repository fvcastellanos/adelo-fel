package net.cavitos.adelo.fel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import net.cavitos.adelo.fel.service.InvoiceService;

@Controller
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;
    
    @GetMapping("/invoices")
    public String generateInvoice() {

        invoiceService.generateElectronicInvoice(1, "123123", "adelo@mailnator.com");

        return "OK";
    }
}
