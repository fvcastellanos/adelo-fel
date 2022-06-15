package net.cavitos.aldelo.fel.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.cavitos.aldelo.fel.domain.fel.ApiInformation;
import net.cavitos.aldelo.fel.domain.fel.FelInformation;
import net.cavitos.aldelo.fel.domain.fel.GeneratorInformation;
import net.cavitos.aldelo.fel.domain.fel.Phrase;

public class ConfigurationServiceTest {

    private ConfigurationService configurationService;

    @BeforeEach
    void setup() {
        String configFile = System.getenv("ALDELO_FEL_CONFIGURATION_FILE");
        configurationService = new ConfigurationService(configFile, new ObjectMapper());
    }

    @Test
    void testLoadConfigFile() {

        Optional<FelInformation> configurationHolder = configurationService.loadConfiguration();
        Assertions.assertThat(configurationHolder)
            .isNotEmpty()
            .hasValue(buildFelInformation());
    }

    // ---------------------------------------------------------------------------------------------------------

    private FelInformation buildFelInformation() {

        FelInformation info = new FelInformation();
        info.setCurrencyCode("GTQ");
        info.setDocumentType("FACT");
        info.setExportation("");
        info.setPerson("");
        info.setGenerator(buildGeneratorInformation());
        info.setApiInformation(buildApiInformation());

        return info;
    }

    private GeneratorInformation buildGeneratorInformation() {

        GeneratorInformation generator = new GeneratorInformation();
        generator.setSubscriptionType("GEN");
        generator.setTaxId("11600089K");
        generator.setCode(1);
        generator.setName("DINAMICA_DEMO");
        generator.setEmail("adeloFel@mailnator.com");
        generator.setCountry("GT");
        generator.setState("Guatemala");
        generator.setCity("Guatemala");
        generator.setAddress("CIUDAD Zona: , GUATEMALA, GUATEMALA");
        generator.setPostalCode("0100");
        generator.setCompanyName("DINAMICA_DEMO");
        generator.setPhrases(buildPhrases());

        return generator;
    }

    private List<Phrase> buildPhrases() {

        List<Phrase> phrases = new ArrayList<>();

        Phrase phrase = new Phrase();
        phrase.setType(1);
        phrase.setScenario(1);

        phrases.add(phrase);

        phrase = new Phrase();
        phrase.setType(1);
        phrase.setScenario(2);

        phrases.add(phrase);

        return phrases;
    }

    private ApiInformation buildApiInformation() {

        ApiInformation api = new ApiInformation();

        api.setSalt("PVD");
        api.setUser("DINAMICA_DEMO");
        api.setWebServiceToken("43F9B4F90CEEBC93DC9F2BBF7C22536A");
        api.setSignatureAlias("DINAMICA_DEMO");
        api.setSignatureToken("6dccb2d50879382aeaf6ee218adbc603");

        return api;
    }
}
