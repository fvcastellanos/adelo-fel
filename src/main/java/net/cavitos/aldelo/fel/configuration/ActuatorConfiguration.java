package net.cavitos.aldelo.fel.configuration;

import net.cavitos.aldelo.fel.actuator.InFileHealthActuator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActuatorConfiguration {

    @Bean
    public InFileHealthActuator inFile() {

        return new InFileHealthActuator();
    }
}
