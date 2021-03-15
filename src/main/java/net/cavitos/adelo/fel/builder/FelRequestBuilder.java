package net.cavitos.adelo.fel.builder;

import com.fel.validaciones.documento.*;
import net.cavitos.adelo.fel.domain.fel.FelInformation;
import net.cavitos.adelo.fel.domain.fel.GeneratorInformation;
import net.cavitos.adelo.fel.domain.model.OrderDetail;

import java.text.SimpleDateFormat;
import java.util.*;

public class FelRequestBuilder {

    private static final String ITEM_TYPE = "B";
    private static final String TAX_NAME = "IVA";

    private FelRequestBuilder() {
    }

    public static DocumentoFel buildInvoiceDocument(List<OrderDetail> orderDetails, 
                                              FelInformation felInformation, 
                                              String recipientTaxId,
                                              String recipientName,
                                              String recipientEmail) {

        DocumentoFel document = new DocumentoFel();
        document.setDatos_generales(FelRequestBuilder.buildGeneralInformation(felInformation));
        document.setDatos_emisor(FelRequestBuilder.buildGeneratorInfo(felInformation));
        document.setDatos_receptor(FelRequestBuilder.buildDatosReceptor(recipientTaxId, recipientEmail, recipientName));

        List<Items> items = FelRequestBuilder.items(orderDetails);
        items.forEach(document::setItems);

        List<Frases> phrases = FelRequestBuilder.buildPhrases(felInformation);
        phrases.forEach(document::setFrases);

        document.setImpuestos_resumen(FelRequestBuilder.buildTotalTaxes(orderDetails));
        document.setTotales(FelRequestBuilder.buildTotal(orderDetails));

        return document;
    }

    public static DatosEmisor buildGeneratorInfo(FelInformation felInformation) {

        DatosEmisor datosEmisor = new DatosEmisor();
        GeneratorInformation generator = felInformation.getGenerator();

        datosEmisor.setAfiliacionIVA(generator.getSubscriptionType());
        datosEmisor.setCodigoEstablecimiento(generator.getCode());
        datosEmisor.setCodigoPostal(generator.getPostalCode());
        datosEmisor.setCorreoEmisor(generator.getEmail());
        datosEmisor.setDepartamento(generator.getState());
        datosEmisor.setMunicipio(generator.getCity());
        datosEmisor.setDireccion(generator.getAddress());
        datosEmisor.setNITEmisor(generator.getTaxId());
        datosEmisor.setNombreComercial(generator.getCompanyName());
        datosEmisor.setNombreEmisor(generator.getName());
        datosEmisor.setPais(generator.getCountry());

        return datosEmisor;
    }

    public static DatosGenerales buildGeneralInformation(FelInformation felInformation) {

        DatosGenerales datosGenerales = new DatosGenerales();
        datosGenerales.setCodigoMoneda(felInformation.getCurrencyCode());
        datosGenerales.setFechaHoraEmision(generateDocumentDate());
        datosGenerales.setNumeroAcceso(felInformation.getAccessNumber());
        datosGenerales.setTipo(felInformation.getDocumentType());
        datosGenerales.setExportacion(felInformation.getExportation());

        return datosGenerales;
    }

    public static List<Items> items(List<OrderDetail> orderDetails) {

        List<Items> list = new ArrayList<>();
        for(int line = 0; line < orderDetails.size(); line++) {

            OrderDetail detail = orderDetails.get(line);
            Items item = new Items();

            item.setNumeroLinea(line + 1);
            item.setBienOServicio(ITEM_TYPE);
            item.setCantidad(detail.getQuantity());
            item.setDescripcion(detail.getItemText());
            item.setDescuento(detail.getDiscountAmount());
            item.setPrecio(detail.getUnitPrice());
            item.setPrecioUnitario(detail.getUnitPrice());
            item.setUnidadMedida("UND");
            item.setTotal(detail.getQuantity() * detail.getUnitPrice());

            item.setImpuestos_detalle(buildItemTaxDetail(item.getTotal(), item.getCantidad()));

            list.add(item);
        }

        return list;
    }

    public static TotalImpuestos buildTotalTaxes(List<OrderDetail> orderDetails) {

        TotalImpuestos totalImpuestos = new TotalImpuestos();

        double total = getTotal(orderDetails);
        double totalTax = total - (total / 1.12);

        totalImpuestos.setTotalMontoImpuesto(totalTax);
        totalImpuestos.setNombreCorto(TAX_NAME);

        return totalImpuestos;
    }

    public static Totales buildTotal(List<OrderDetail> orderDetails) {

        Totales total = new Totales();
        total.setGranTotal(getTotal(orderDetails));

        return total;
    }

    public static List<Frases> buildPhrases(FelInformation felInformation) {
        
        GeneratorInformation generator = felInformation.getGenerator();

        List<Frases> list = new ArrayList<>();

        generator.getPhrases().forEach(p -> {

            Frases frases = new Frases();
            frases.setCodigoEscenario(p.getScenario());
            frases.setTipoFrase(p.getType());

            list.add(frases);
        });

        return list;
    }

    public static DatosReceptor buildDatosReceptor(String taxId, String name, String email) {

        DatosReceptor datosReceptor = new DatosReceptor();
        datosReceptor.setIDReceptor(taxId);
        datosReceptor.setCorreoReceptor(email);
        datosReceptor.setCodigoPostal("0000");
        datosReceptor.setDepartamento("Ciudad");
        datosReceptor.setDireccion("Ciudad");
        datosReceptor.setMunicipio("Ciudad");
        datosReceptor.setNombreReceptor(name);
        datosReceptor.setPais("GT");
        datosReceptor.setTipoEspecial("");

        return datosReceptor;
    }

    // -------------------------------------------------------------------------------------------------------------------

    private static Double getTotal(List<OrderDetail> orderDetails) {

        return orderDetails.stream()
            .mapToDouble(detail -> detail.getQuantity() * detail.getUnitPrice())
            .sum();
    }

    private static ImpuestosDetalle buildItemTaxDetail(double itemTotal, double quantity) {

        ImpuestosDetalle detalle = new ImpuestosDetalle();
        detalle.setCodigoUnidadGravable(1);
        detalle.setMontoGravable(itemTotal / 1.12);
        detalle.setMontoImpuesto(itemTotal - detalle.getMontoGravable());
        detalle.setNombreCorto(TAX_NAME);
        detalle.setCantidadUnidadesGravables(quantity);

        return detalle;
    }

    private static String generateDocumentDate() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'-06:00'", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("America/Guatemala"));

        return sdf.format(new Date());
    }
}
