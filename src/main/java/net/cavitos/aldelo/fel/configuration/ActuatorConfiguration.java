package net.cavitos.aldelo.fel.configuration;

import net.cavitos.aldelo.fel.actuator.InFileHealthActuator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActuatorConfiguration {

    @Value("${infile.api.certification.echo.url}")
    private String certificateEchoUrl;

    @Value("${infile.api.sign.echo.url}")
    private String signatureEchoUrl;

    @Bean
    public InFileHealthActuator inFile() {

        return new InFileHealthActuator(certificateEchoUrl, signatureEchoUrl);
    }
}
