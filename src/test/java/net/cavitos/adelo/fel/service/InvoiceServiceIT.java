package net.cavitos.adelo.fel.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class InvoiceServiceIT {

    @Autowired
    private InvoiceService invoiceService;

    @Test
    void foo() {

        invoiceService.generateElectronicInvoice(1, "12345678", "recipient@mailnator.com");
    }
    
}
