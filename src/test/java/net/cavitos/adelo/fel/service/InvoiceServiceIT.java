package net.cavitos.adelo.fel.service;

import io.vavr.control.Either;
import net.cavitos.adelo.fel.domain.fel.InvoiceInformation;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class InvoiceServiceIT {

    @Autowired
    private InvoiceService invoiceService;

    @Test
    void foo() {

        Either<List<String>, InvoiceInformation> result = invoiceService.generateElectronicInvoice(1,
                "12345678", "Juan Penas", "recipient@mailnator.com");

        Assertions.assertThat(result.isRight())
                .isTrue();
    }
    
}
