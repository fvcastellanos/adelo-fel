package net.cavitos.aldelo.fel.service;

import net.cavitos.aldelo.fel.domain.fel.InvoiceGeneration;
import net.cavitos.aldelo.fel.domain.fel.InvoiceInformation;
import net.cavitos.aldelo.fel.domain.fel.InvoiceType;
import net.cavitos.aldelo.fel.domain.model.OrderDetail;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;

@SpringBootTest
public class InvoiceServiceIT {

    @Autowired
    private InvoiceService invoiceService;

    @Test
    void testInvoiceCertification() {

        final InvoiceInformation result = invoiceService.generateElectronicInvoice(buildInvoiceGeneration(), InvoiceType.BAR);
    }

    // --------------------------------------------------------------------------------------------------------------

    private InvoiceGeneration buildInvoiceGeneration() {

        OrderDetail orderDetail = OrderDetail.builder()
                .orderId(1)
                .itemId(1)
                .quantity(5)
                .unitPrice(23.21)
                .itemText("Cafe Late deslactosado")
                .build();

        return InvoiceGeneration.builder()
                .orderId(1)
                .taxId("CF")
                .name("Juan Penas")
                .email("adeloTest@mailnator.com")
                .details(Collections.singletonList(orderDetail))
                .build();
    }
}
