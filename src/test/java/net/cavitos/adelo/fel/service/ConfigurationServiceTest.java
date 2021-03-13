package net.cavitos.adelo.fel.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.cavitos.adelo.fel.domain.fel.ApiInformation;
import net.cavitos.adelo.fel.domain.fel.FelInformation;
import net.cavitos.adelo.fel.domain.fel.GeneratorInformation;
import net.cavitos.adelo.fel.domain.fel.Phrase;

public class ConfigurationServiceTest {

    private ConfigurationService configurationService;

    @BeforeEach
    void setup() {
        String configFile = System.getenv("ADELO_FEL_CONFIGURATION_FILE");
        configurationService = new ConfigurationService(configFile, new ObjectMapper());
    }

    @Test
    void testLoadConfigFile() {

        Optional<FelInformation> configuration = configurationService.loadConfiguration();
        Assertions.assertThat(configuration)
            .isNotEmpty()
            .hasValue(buildFelInformation());
    }

    // ---------------------------------------------------------------------------------------------------------

    private FelInformation buildFelInformation() {

        FelInformation info = new FelInformation();
        info.setCurrencyCode("GTQ");
        info.setDocumentType("FACT");
        info.setGenerator(buildGeneratorInformation());
        info.setApiInformation(buildApiInformation());

        return info;
    }

    private GeneratorInformation buildGeneratorInformation() {

        GeneratorInformation generator = new GeneratorInformation();
        generator.setSubscriptionType("GEN");
        generator.setTaxId("1000000000K");
        generator.setCode(1);
        generator.setName("Usuario de Pruebas IT");
        generator.setEmail("adeloFel@mailnator.com");
        generator.setCountry("");
        generator.setState("");
        generator.setAddress("Ciudad 00-00 Ciudad Zona: 0, Guatemala, Guatemala");
        generator.setPostalCode("");
        generator.setCompanyName("");
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
        api.setUser("DEMO_FEL");
        api.setWebServiceToken("E5DC9FFBA5F3653E27DF2FC1DCAC824D");
        api.setSignatureAlias("DEMO_FEL");
        api.setSignatureToken("9c748d9bcf1455655b9e9a5c34525570");

        return api;
    }
}
