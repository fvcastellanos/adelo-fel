package net.cavitos.aldelo.fel.service;

import com.fel.firma.emisor.RespuestaServicioFirma;
import com.fel.validaciones.documento.DescripcionErrores;
import com.fel.validaciones.documento.DocumentoFel;
import com.fel.validaciones.documento.GenerarXml;
import com.fel.validaciones.documento.Respuesta;
import com.fel.validaciones.documento.RespuestaServicioFel;
import io.vavr.control.Either;
import net.cavitos.aldelo.fel.builder.BusinessExceptionBuilder;
import net.cavitos.aldelo.fel.builder.FelRequestBuilder;
import net.cavitos.aldelo.fel.client.InFileClient;
import net.cavitos.aldelo.fel.domain.fel.ApiInformation;
import net.cavitos.aldelo.fel.domain.fel.FelInformation;
import net.cavitos.aldelo.fel.domain.fel.Generator;
import net.cavitos.aldelo.fel.domain.fel.GeneratorInformation;
import net.cavitos.aldelo.fel.domain.fel.InvoiceGeneration;
import net.cavitos.aldelo.fel.domain.fel.InvoiceInformation;
import net.cavitos.aldelo.fel.domain.fel.InvoiceType;
import net.cavitos.aldelo.fel.domain.model.OrderDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class InvoiceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceService.class);

    private static final String OLD_XSD = "http://www.sat.gob.gt/dte/fel/0.1.0";
    private static final String NEW_XSD = "http://www.sat.gob.gt/dte/fel/0.2.0";

    private static final String OLD_XSD_VERSION = "Version=\"0.4\"";
    private static final String NEW_XSD_VERSION = "Version=\"0.1\"";

    private final InFileClient inFileClient;
    private final ConfigurationService configurationService;

    public InvoiceService(ConfigurationService configurationService, InFileClient inFileClient) {

        this.inFileClient = inFileClient;
        this.configurationService = configurationService;
    }
    
    public InvoiceInformation generateElectronicInvoice(final InvoiceGeneration invoiceGeneration, final InvoiceType invoiceType) {

        final long orderId = invoiceGeneration.getOrderId();
        LOGGER.info("generating invoice for orderId: {}", orderId);

        final FelInformation configuration = configurationService.loadConfiguration()
            .orElseThrow(() -> {

                LOGGER.error("can't load configuration file");
                return BusinessExceptionBuilder.createBusinessException("can't load configuration file");    
            });

        final List<OrderDetail> orderDetails = invoiceGeneration.getDetails();

        if (orderDetails.isEmpty()) {

            LOGGER.error("no order details found for orderId: {}", orderId);
            throw BusinessExceptionBuilder.createBusinessException("no order details found for order id: %s", orderId);
        }

        final DocumentoFel document = FelRequestBuilder.buildInvoiceDocument(invoiceGeneration, configuration, invoiceType);

        final GeneratorInformation generatorInformation = getGeneratorInformationByType(configuration, invoiceType);

        final Either<List<String>, InvoiceInformation> result = buildXmlDocument(document)
            .flatMap(xml -> signXmlDocument(xml, generatorInformation.getApiInformation()))
            .flatMap(file -> generateInvoice(file, invoiceGeneration.getEmail(), configuration, invoiceType));

        return result
            .getOrElseThrow(errors -> BusinessExceptionBuilder.createBusinessException(HttpStatus.UNPROCESSABLE_ENTITY, 
                "No es posible generar DTE", errors));
    }

    // --------------------------------------------------------------------------------------------------------------

    private Either<List<String>, String> buildXmlDocument(DocumentoFel document) {

        try {

            LOGGER.info("creating xml document");

            GenerarXml xml = new GenerarXml();
            Respuesta response = xml.ToXml(document);

            // Replace hard coded xsd version since Infile has provided outdated library that doesn't comply against
            // SAT specifications for DTE xml document
            String updatedXml = response.getXml()
                    .replaceAll(OLD_XSD, NEW_XSD)
                    .replaceAll(OLD_XSD_VERSION, NEW_XSD_VERSION);

            if (response.getResultado()) {

                LOGGER.info("xml document generated");
                return Either.right(updatedXml);
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

    private Either<List<String>, InvoiceInformation> generateInvoice(final String signedDocument,
                                                                     final String recipientEmail,
                                                                     final FelInformation felInformation,
                                                                     final InvoiceType invoiceType) {
        try {

            final RespuestaServicioFel respuestaServicioFel = inFileClient.certificateDocument(signedDocument, 
                recipientEmail, getGeneratorInformationByType(felInformation, invoiceType));

            if (respuestaServicioFel.getResultado()) {

                InvoiceInformation invoiceInformation = InvoiceInformation.builder()
                        .type(invoiceType.name())
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

    private GeneratorInformation getGeneratorInformationByType(final FelInformation felInformation, final InvoiceType invoiceType) {

        final Generator generator = felInformation.getGenerator();

        return  InvoiceType.BAR == invoiceType ? generator.getBarSubscription()
            : generator.getRestaurantSubscription();
    }
}
