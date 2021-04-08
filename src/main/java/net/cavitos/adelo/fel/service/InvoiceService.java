package net.cavitos.adelo.fel.service;

import com.fel.firma.emisor.RespuestaServicioFirma;
import com.fel.validaciones.documento.DescripcionErrores;
import com.fel.validaciones.documento.DocumentoFel;
import com.fel.validaciones.documento.GenerarXml;
import com.fel.validaciones.documento.Respuesta;
import com.fel.validaciones.documento.RespuestaServicioFel;
import io.vavr.control.Either;
import net.cavitos.adelo.fel.builder.FelRequestBuilder;
import net.cavitos.adelo.fel.client.InFileClient;
import net.cavitos.adelo.fel.domain.fel.ApiInformation;
import net.cavitos.adelo.fel.domain.fel.FelInformation;
import net.cavitos.adelo.fel.domain.fel.InvoiceGeneration;
import net.cavitos.adelo.fel.domain.fel.InvoiceInformation;
import net.cavitos.adelo.fel.domain.model.OrderDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class InvoiceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceService.class);

    private final InFileClient inFileClient;
    private final ConfigurationService configurationService;

    public InvoiceService(ConfigurationService configurationService, InFileClient inFileClient) {

        this.inFileClient = inFileClient;
        this.configurationService = configurationService;
    }
    
    public Either<List<String>, InvoiceInformation> generateElectronicInvoice(InvoiceGeneration invoiceGeneration) {

        final long orderId = invoiceGeneration.getOrderId();
        LOGGER.info("generating invoice for orderId: {}", orderId);

        Optional<FelInformation> configurationHolder = configurationService.loadConfiguration();

        if (!configurationHolder.isPresent()) {

            LOGGER.error("can't load configuration file");
            return Either.left(Collections.singletonList("can't load configuration file"));
        }

        List<OrderDetail> orderDetails = invoiceGeneration.getDetails();

        if (orderDetails.isEmpty()) {

            LOGGER.error("no order details found for orderId: {}", orderId);
            return Either.left(Collections.singletonList("no order details found for order id: " + orderId));
        }

        FelInformation configuration = configurationHolder.get();
        DocumentoFel document = FelRequestBuilder.buildInvoiceDocument(orderDetails, configuration, invoiceGeneration.getTaxId(),
                invoiceGeneration.getName(), invoiceGeneration.getEmail());

        return buildXmlDocument(document)
                .flatMap(xml -> signXmlDocument(xml, configuration.getApiInformation()))
                .flatMap(file -> generateInvoice(file, invoiceGeneration.getEmail(), configuration));
    }

    // --------------------------------------------------------------------------------------------------------------

    private Either<List<String>, String> buildXmlDocument(DocumentoFel document) {

        try {

            LOGGER.info("creating xml document");

            GenerarXml xml = new GenerarXml();
            Respuesta response = xml.ToXml(document);

            if (response.getResultado()) {

                LOGGER.info("xml document generated");
                return Either.right(response.getXml());
            }

            return Either.left(response.getErrores());
        } catch (Exception exception) {

            LOGGER.error("can't generate xml - ", exception);
            return Either.left(Collections.singletonList("can't generate xml document"));
        }
    }

    private Either<List<String>, String> signXmlDocument(String xml, ApiInformation apiInformation) {

        try {

            LOGGER.info("signing xml document");
            RespuestaServicioFirma signResponse = inFileClient.signDocument(xml, apiInformation.getSignatureAlias(), apiInformation.getSignatureToken());

            if (signResponse.isResultado()) {

                LOGGER.info("xml document signed");
                return Either.right(signResponse.getArchivo());
            }

            LOGGER.error(signResponse.getDescripcion());
            return Either.left(Collections.singletonList(signResponse.getDescripcion()));

        } catch (Exception exception) {

            LOGGER.error("can't sign xml document - ", exception);
            return Either.left(Collections.singletonList("can't sign xml document"));
        }
    }

    private Either<List<String>, InvoiceInformation> generateInvoice(String signedDocument,
                                                                     String recipientEmail,
                                                                     FelInformation felInformation) {
        try {

            RespuestaServicioFel respuestaServicioFel = inFileClient.certificateDocument(signedDocument, recipientEmail, felInformation);

            if (respuestaServicioFel.getResultado()) {

                InvoiceInformation invoiceInformation = InvoiceInformation.builder()
                        .origin(respuestaServicioFel.getOrigen())
                        .description(respuestaServicioFel.getDescripcion())
                        .information(respuestaServicioFel.getInfo())
                        .date(respuestaServicioFel.getFecha())
                        .uuid(respuestaServicioFel.getUuid())
                        .correlative(respuestaServicioFel.getSerie())
                        .number(respuestaServicioFel.getNumero())
                        .build();

                return Either.right(invoiceInformation);
            }

            List<String> errors = respuestaServicioFel.getDescripcion_errores()
                    .stream()
                    .map(DescripcionErrores::getMensaje_error)
                    .collect(Collectors.toList());

            return Either.left(errors);


        } catch (Exception exception) {

            LOGGER.error("can't generate invoice - ", exception);
            return Either.left(Collections.singletonList("can't generate invoice"));
        }
    }
}
