package net.cavitos.adelo.fel.service;

import com.fel.validaciones.documento.DocumentoFel;
import com.fel.validaciones.documento.GenerarXml;
import com.fel.validaciones.documento.Respuesta;

import net.cavitos.adelo.fel.domain.fel.FelInformation;
import net.cavitos.adelo.fel.domain.model.OrderDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vavr.control.Either;
import net.cavitos.adelo.fel.builder.FelRequestBuilder;
import net.cavitos.adelo.fel.repository.OrderRepository;

import java.util.List;
import java.util.Optional;

public class InvoiceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceService.class);

    private final ConfigurationService configurationService;
    private final OrderRepository orderRepository;

    public InvoiceService(ConfigurationService configurationService, OrderRepository orderRepository) {

        this.configurationService = configurationService;
        this.orderRepository = orderRepository;
    }
    
    public Either<String, Boolean> generateElectronicInvoice(long orderId, String recipientTaxId, String recipientEmail) {

        LOGGER.info("generating invoice for orderId: {}", orderId);

        Optional<FelInformation> configurationHolder = configurationService.loadConfiguration();

        if (!configurationHolder.isPresent()) {

            LOGGER.error("can't load configuration file");
            return Either.left("can't load configuration file");
        }

        List<OrderDetail> orderDetails = orderRepository.getOrderDetails(orderId);

        if (orderDetails.isEmpty()) {

            LOGGER.error("no order details found for orderId: {}", orderId);
            return Either.left("no order details found for order id: " + orderId);
        }

        FelInformation configuration = configurationHolder.get();
        DocumentoFel document = FelRequestBuilder.buildInvoiceDocument(orderDetails, configuration, recipientTaxId, recipientEmail);

        foo(document);

        return Either.right(true);
    }

    // --------------------------------------------------------------------------------------------------------------

    private void foo(DocumentoFel document) {

        try {

            GenerarXml xml = new GenerarXml();
            Respuesta response = xml.ToXml(document);

            if (response.getResultado()) {

                LOGGER.info(response.getXml());
            }

        } catch (Exception exception) {

            LOGGER.error("can't generate xml - ", exception);
        }

    }
}
