package net.cavitos.adelo.fel.service;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.cavitos.adelo.fel.domain.fel.FelInformation;

public class ConfigurationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationService.class);

    private final String configurationFile;
    private final ObjectMapper objectMapper;

    public ConfigurationService(String configurationFile, ObjectMapper objectMapper) {

        this.configurationFile = configurationFile;
        this.objectMapper = objectMapper;
    }

    public Optional<FelInformation> loadConfiguration() {

        try {

            File file = new File(configurationFile);
            FelInformation felInformation = objectMapper.readValue(file, FelInformation.class);

            return Optional.of(felInformation);
        } catch (IOException exception) {

            LOGGER.error("can't load configuration file - ", exception);
        }

        return Optional.empty();
    }

}
