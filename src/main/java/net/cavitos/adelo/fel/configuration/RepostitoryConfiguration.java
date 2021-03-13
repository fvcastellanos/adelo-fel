package net.cavitos.adelo.fel.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.cavitos.adelo.fel.repository.OrderRepository;

@Configuration
public class RepostitoryConfiguration {
    
    @Bean
    public OrderRepository orderRepository(@Value("${adelo.fel.jdbc.connection.string}") String connectionString) {

        return new OrderRepository(connectionString);
    }
}
