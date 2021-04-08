package net.cavitos.aldelo.fel.actuator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

public class InFileHealthActuator implements HealthIndicator {

    @Override
    public Health health() {
        return Health.up().build();
    }
}
