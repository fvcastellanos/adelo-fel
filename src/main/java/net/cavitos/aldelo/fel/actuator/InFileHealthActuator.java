package net.cavitos.aldelo.fel.actuator;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

public class InFileHealthActuator implements HealthIndicator {

    private static final Logger LOGGER = LoggerFactory.getLogger(InFileHealthActuator.class);

    private static final String UP_STATUS = "UP";
    private static final String DOWN_STATUS = "DOWN";

    private final String certificateEchoUrl;
    private final String signatureEchoUrl;

    public InFileHealthActuator(final String certificateEchoUrl, final String signatureEchoUrl) {

        this.certificateEchoUrl = certificateEchoUrl;
        this.signatureEchoUrl = signatureEchoUrl;
    }

    @Override
    public Health health() {

        String certificateHealth = testUrl(certificateEchoUrl);
        String signatureHealth = testUrl(signatureEchoUrl);

        if (UP_STATUS.equals(certificateHealth) && UP_STATUS.equals(signatureHealth)) {
            return Health.up()
                    .withDetail("certificate", certificateHealth)
                    .withDetail("signature", signatureHealth)
                    .build();
        }

        return Health.down()
                .withDetail("certificate", certificateHealth)
                .withDetail("signature", signatureHealth)
                .build();
    }

    private String testUrl(String url) {

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            LOGGER.info("testing url: {}", url);
            final HttpGet httpGet = new HttpGet(url);

            return httpClient.execute(httpGet, (response) -> {

                final int status = response.getStatusLine().getStatusCode();

                if (status == HttpStatus.SC_OK) {

                    LOGGER.info("url: {} is up", url);
                    return UP_STATUS;
                }

                LOGGER.warn("unable to test url: {}", url);
                return DOWN_STATUS;
            });

        } catch (Exception exception) {

            LOGGER.warn("unable to test url: {} - ", url, exception);
            return DOWN_STATUS;
        }
    }
}
