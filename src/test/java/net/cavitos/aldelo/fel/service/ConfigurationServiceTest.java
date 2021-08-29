package net.cavitos.aldelo.fel.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.cavitos.aldelo.fel.domain.fel.FelInformation;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Optional;

public class ConfigurationServiceTest {

    private ConfigurationService configurationService;
    private String configFile;

    @BeforeEach
    void setup() {
        configFile = System.getenv("ALDELO_FEL_CONFIGURATION_FILE");
        configurationService = new ConfigurationService(configFile, new ObjectMapper());
    }

    @Test
    void testLoadConfigFile() {

        FelInformation expectedConfiguration = readConfigFile();

        Optional<FelInformation> configurationHolder = configurationService.loadConfiguration();
        Assertions.assertThat(configurationHolder)
            .isNotEmpty()
            .hasValue(expectedConfiguration);
    }

    // ---------------------------------------------------------------------------------------------------------

    private FelInformation readConfigFile() {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            File file = new File(configFile);
            return objectMapper.readValue(file, FelInformation.class);
        } catch (Exception ex) {

            ex.printStackTrace();
            return null;
        }
    }
}
