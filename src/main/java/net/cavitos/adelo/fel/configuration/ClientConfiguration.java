package net.cavitos.adelo.fel.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.cavitos.adelo.fel.client.InFileClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfiguration {

    @Value("${infile.api.certification.url}")
    private String certificationApiUrl;

    @Value("${infile.api.sign.url}")
    private String signApiUrl;

    @Bean
    public InFileClient inFileClient(ObjectMapper objectMapper) {

        return new InFileClient(certificationApiUrl, signApiUrl, objectMapper);
    }
}
