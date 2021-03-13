package net.cavitos.adelo.fel.service;

import com.fel.firma.emisor.FirmaEmisor;
import com.fel.firma.emisor.RespuestaServicioFirma;
import com.fel.validaciones.documento.*;

import net.cavitos.adelo.fel.domain.fel.ApiInformation;
import net.cavitos.adelo.fel.domain.fel.FelInformation;
import net.cavitos.adelo.fel.domain.fel.GeneratorInformation;
import net.cavitos.adelo.fel.domain.fel.InvoiceInformation;
import net.cavitos.adelo.fel.domain.model.OrderDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vavr.control.Either;
import net.cavitos.adelo.fel.builder.FelRequestBuilder;
import net.cavitos.adelo.fel.repository.OrderRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class InvoiceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceService.class);

    private final ConfigurationService configurationService;
    private final OrderRepository orderRepository;

    public InvoiceService(ConfigurationService configurationService, OrderRepository orderRepository) {

        this.configurationService = configurationService;
        this.orderRepository = orderRepository;
    }
    
    public Either<List<String>, InvoiceInformation> generateElectronicInvoice(long orderId,
                                                             String recipientTaxId,
                                                             String recipientName,
                                                             String recipientEmail) {

        LOGGER.info("generating invoice for orderId: {}", orderId);

        Optional<FelInformation> configurationHolder = configurationService.loadConfiguration();

        if (!configurationHolder.isPresent()) {

            LOGGER.error("can't load configuration file");
            return Either.left(Collections.singletonList("can't load configuration file"));
        }

        List<OrderDetail> orderDetails = orderRepository.getOrderDetails(orderId);

        if (orderDetails.isEmpty()) {

            LOGGER.error("no order details found for orderId: {}", orderId);
            return Either.left(Collections.singletonList("no order details found for order id: " + orderId));
        }

        FelInformation configuration = configurationHolder.get();
        DocumentoFel document = FelRequestBuilder.buildInvoiceDocument(orderDetails, configuration, recipientTaxId,
                recipientName, recipientEmail);

        return buildXmlDocument(document)
                .flatMap(xml -> signXmlDocument(xml, configuration.getApiInformation()))
                .flatMap(file -> generateInvoice(file, configuration));
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
            FirmaEmisor firmaEmisor = new FirmaEmisor();
            RespuestaServicioFirma signResponse = firmaEmisor.Firmar(xml, apiInformation.getSignatureAlias(), apiInformation.getSignatureToken());

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

    private Either<List<String>, InvoiceInformation> generateInvoice(String file, FelInformation felInformation) {

        try {

            GeneratorInformation generator = felInformation.getGenerator();

            ConexionServicioFel conexionServicioFel = buildConnection(felInformation);

            ServicioFel service = new ServicioFel();
            RespuestaServicioFel respuestaServicioFel = service.Certificar(conexionServicioFel, file,
                    generator.getTaxId(), "N/A", "CERTIFICACION");

            if (respuestaServicioFel.getResultado()) {

                InvoiceInformation invoiceInformation = InvoiceInformation.builder()
                        .origin(respuestaServicioFel.getOrigen())
                        .description(respuestaServicioFel.getDescripcion())
                        .information(respuestaServicioFel.getInfo())
                        .date(respuestaServicioFel.getFecha())
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

    private ConexionServicioFel buildConnection(FelInformation felInformation) {

        ApiInformation apiInformation = felInformation.getApiInformation();

        ConexionServicioFel conexionServicioFel = new ConexionServicioFel();
        conexionServicioFel.setUrl("");
        conexionServicioFel.setMetodo("POST");
        conexionServicioFel.setContent_type("application/json");
        conexionServicioFel.setUsuario(apiInformation.getUser());
        conexionServicioFel.setLlave(apiInformation.getWebServiceToken());
        conexionServicioFel.setIdentificador(apiInformation.getSalt() + ":" + UUID.randomUUID().toString());

        return conexionServicioFel;
    }
}
