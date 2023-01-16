package net.cavitos.aldelo.fel.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fel.firma.emisor.RequestServicioFirma;
import com.fel.firma.emisor.RespuestaServicioFirma;
import com.fel.validaciones.documento.RequestServicioFel;
import com.fel.validaciones.documento.RespuestaServicioFel;
import net.cavitos.aldelo.fel.domain.fel.ApiInformation;
import net.cavitos.aldelo.fel.domain.fel.GeneratorInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

public class InFileClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(InFileClient.class);

    private static final String USER_HEADER = "usuario";
    private static final String KEY_HEADER = "llave";
    private static final String ID_HEADER = "identificador";

    private final String certificationApiUrl;
    private final String signApiUrl;

    private final ObjectMapper objectMapper;

    private final RestTemplate restTemplate;

    public InFileClient(String certificationApiUrl, String signApiUrl, ObjectMapper objectMapper) {

        this.certificationApiUrl = certificationApiUrl;
        this.signApiUrl = signApiUrl;
        this.restTemplate = new RestTemplate();
        this.objectMapper = objectMapper;
    }

    public RespuestaServicioFel certificateDocument(final String signedDocument,
                                                    final String recipientEmail,
                                                    final GeneratorInformation generatorInformation) {
        
        final ApiInformation apiInformation = generatorInformation.getApiInformation();

        final RequestServicioFel requestBody = new RequestServicioFel();
        requestBody.setCorreo_copia(recipientEmail);
        requestBody.setNit_emisor(generatorInformation.getTaxId());
        requestBody.setXml_dte(signedDocument);

        final String documentId = apiInformation.getSalt() + ":" + UUID.randomUUID();

        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, "application/json");
        httpHeaders.set(USER_HEADER, apiInformation.getUser());
        httpHeaders.set(KEY_HEADER, apiInformation.getWebServiceToken());
        httpHeaders.set(ID_HEADER, documentId);

        LOGGER.info("Certificate document: {} against electronic invoice provider", documentId);

        final HttpEntity<RequestServicioFel> httpEntity = new HttpEntity<>(requestBody, httpHeaders);
        final ResponseEntity<String> response = postRequest(certificationApiUrl, httpEntity);

        if (response.getStatusCode().is2xxSuccessful()) {

            return buildCertificationResponse(response.getBody());
        }

        return buildCertificationErrorResponse(response.getBody());
    }

    public RespuestaServicioFirma signDocument(String document, String signatureAlias, String signatureToken) {

        String encodedDocument = Base64.getEncoder()
                .encodeToString(document.getBytes(StandardCharsets.UTF_8));

        RequestServicioFirma signRequest = new RequestServicioFirma();
        signRequest.setAlias(signatureAlias);
        signRequest.setArchivo(encodedDocument);
        signRequest.setCodigo("");
        signRequest.setEs_anulacion("N");
        signRequest.setLlave(signatureToken);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, "application/json");

        LOGGER.info("Sign document against electronic invoice provider");

        HttpEntity<RequestServicioFirma> httpEntity = new HttpEntity<>(signRequest, httpHeaders);
        ResponseEntity<String> response = postRequest(signApiUrl, httpEntity);

        if (response.getStatusCode().is2xxSuccessful()) {

            return buildSignResponse(response.getBody());
        }

        return buildSignErrorResponse(response.getBody());
    }

    // --------------------------------------------------------------------------------------------------------

    private <T> ResponseEntity<String> postRequest(String url, HttpEntity<T> httpEntity) {

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, httpEntity, String.class);
        LOGGER.info("Response status: {}", responseEntity.getStatusCode());

        return responseEntity;
    }

    private RespuestaServicioFel buildCertificationResponse(String content) {

        try {
            return objectMapper.readValue(content, RespuestaServicioFel.class);
        } catch (Exception exception) {

            LOGGER.error("can't build certification response - ", exception);
            return buildCertificationErrorResponse("can't parse certification response from provider");
        }
    }

    private RespuestaServicioFirma buildSignResponse(String content) {

        try {
            return objectMapper.readValue(content, RespuestaServicioFirma.class);
        } catch (Exception exception) {

            LOGGER.error("can't build signing response - ", exception);
            return buildSignErrorResponse("can't parse signing response from provider");
        }
    }

    private RespuestaServicioFel buildCertificationErrorResponse(String content) {

        RespuestaServicioFel respuestaServicioFel = new RespuestaServicioFel();
        respuestaServicioFel.setResultado(false);
        respuestaServicioFel.setDescripcion(content);

        return respuestaServicioFel;
    }

    private RespuestaServicioFirma buildSignErrorResponse(String content) {

        RespuestaServicioFirma respuestaServicioFirma = new RespuestaServicioFirma();
        respuestaServicioFirma.setResultado(false);
        respuestaServicioFirma.setDescripcion(content);

        return respuestaServicioFirma;
    }
}
