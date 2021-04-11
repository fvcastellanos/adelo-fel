package net.cavitos.aldelo.fel.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.cavitos.aldelo.fel.client.InFileClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.cavitos.aldelo.fel.service.ConfigurationService;
import net.cavitos.aldelo.fel.service.InvoiceService;

@Configuration
public class ServiceConfiguration {

    @Bean
    public ConfigurationService configurationService(@Value("${aldelo.fel.configuration.file}") String configurationFile,
                                                     ObjectMapper objectMapper) {

        return new ConfigurationService(configurationFile, objectMapper);
    }    

    @Bean
    public InvoiceService invoiceService(ConfigurationService configurationService,                                         
                                         InFileClient inFileClient) {

        return new InvoiceService(configurationService, inFileClient);
    }
}
