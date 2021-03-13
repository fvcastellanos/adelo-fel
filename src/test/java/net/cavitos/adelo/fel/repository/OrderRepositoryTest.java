package net.cavitos.adelo.fel.repository;

import net.cavitos.adelo.fel.domain.model.OrderDetail;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class OrderRepositoryTest {

    private OrderRepository invoiceRepository;

    @BeforeEach
    public void setup() {
        String connectionString = System.getenv("ADELO_FEL_JDBC_CONNECTION_STRING");
        invoiceRepository = new OrderRepository(connectionString);
    }

    @Test
    public void testGetOrderDetails() {

        List<OrderDetail> details = invoiceRepository.getOrderDetails(1);

        Assertions.assertThat(details)
            .isNotEmpty();
    }    
}
