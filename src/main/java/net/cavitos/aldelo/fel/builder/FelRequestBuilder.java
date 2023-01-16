package net.cavitos.aldelo.fel.builder;

import net.cavitos.aldelo.fel.domain.fel.FelInformation;
import net.cavitos.aldelo.fel.domain.fel.Generator;
import net.cavitos.aldelo.fel.domain.fel.GeneratorInformation;
import net.cavitos.aldelo.fel.domain.fel.InvoiceGeneration;
import net.cavitos.aldelo.fel.domain.fel.InvoiceType;
import net.cavitos.aldelo.fel.domain.model.OrderDetail;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.fel.validaciones.documento.Adendas;
import com.fel.validaciones.documento.DatosEmisor;
import com.fel.validaciones.documento.DatosGenerales;
import com.fel.validaciones.documento.DatosReceptor;
import com.fel.validaciones.documento.DocumentoFel;
import com.fel.validaciones.documento.Frases;
import com.fel.validaciones.documento.ImpuestosDetalle;
import com.fel.validaciones.documento.Items;
import com.fel.validaciones.documento.TotalImpuestos;
import com.fel.validaciones.documento.Totales;

public class FelRequestBuilder {

    private static final String ITEM_TYPE = "B";
    private static final String TAX_NAME = "IVA";
    private static final String TIP_ENTRY_NAME = "propina";
    private static final String TOTAL_INVOICE_PLUS_TIP_ENTRY_NAME = "totalmasprop";
    private static final String ORDER_ID_ENTRY_NAME = "cnt_int";

    private FelRequestBuilder() {
    }

    public static DocumentoFel buildInvoiceDocument(final InvoiceGeneration invoiceGeneration,
                                                    final FelInformation felInformation,
                                                    final InvoiceType invoiceType) {

        final List<OrderDetail> orderDetails = invoiceGeneration.getDetails();

        final DocumentoFel document = new DocumentoFel();
        document.setDatos_generales(FelRequestBuilder.buildGeneralInformation(felInformation));
        document.setDatos_emisor(FelRequestBuilder.buildGeneratorInfo(felInformation, invoiceType));
        document.setDatos_receptor(FelRequestBuilder.buildDatosReceptor(invoiceGeneration.getTaxId(),
                invoiceGeneration.getTaxIdType(),
                invoiceGeneration.getName(),
                invoiceGeneration.getEmail()));

        final List<Items> items = FelRequestBuilder.items(orderDetails);
        items.forEach(document::setItems);

        final List<Frases> phrases = FelRequestBuilder.buildPhrases(felInformation, invoiceType);
        phrases.forEach(document::setFrases);

        document.setImpuestos_resumen(FelRequestBuilder.buildTotalTaxes(orderDetails));
        document.setTotales(FelRequestBuilder.buildTotal(orderDetails));
        document.setAdenda(buildAdendaPropina(invoiceGeneration.getOrderId(), invoiceGeneration.getTipAmount(), orderDetails));

        return document;

    }

    public static DatosEmisor buildGeneratorInfo(final FelInformation felInformation, final InvoiceType invoiceType) {

        final DatosEmisor datosEmisor = new DatosEmisor();
        final GeneratorInformation generator = getInvoiceGeneratorByType(felInformation, invoiceType);

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

    public static DatosGenerales buildGeneralInformation(final FelInformation felInformation) {

        final DatosGenerales datosGenerales = new DatosGenerales();
        datosGenerales.setCodigoMoneda(felInformation.getCurrencyCode());
        datosGenerales.setFechaHoraEmision(generateDocumentDate());
        datosGenerales.setNumeroAcceso(felInformation.getAccessNumber());
        datosGenerales.setTipo(felInformation.getDocumentType());
        datosGenerales.setExportacion(felInformation.getExportation());

        return datosGenerales;
    }

    public static List<Items> items(final List<OrderDetail> orderDetails) {

        List<Items> list = new ArrayList<>();
        for(int line = 0; line < orderDetails.size(); line++) {

            OrderDetail detail = orderDetails.get(line);
            Items item = new Items();

            item.setNumeroLinea(line + 1);
            item.setBienOServicio(ITEM_TYPE);
            item.setCantidad(detail.getQuantity());
            item.setDescripcion(detail.getItemText());
            item.setDescuento(detail.getDiscountAmount());
            item.setPrecio(detail.getUnitPrice() * detail.getQuantity());
            item.setPrecioUnitario(detail.getUnitPrice());
            item.setUnidadMedida("UND");
            item.setTotal((detail.getQuantity() * detail.getUnitPrice()) - detail.getDiscountAmount());

            item.setImpuestos_detalle(buildItemTaxDetail(item.getTotal()));

            list.add(item);
        }

        return list;
    }

    public static TotalImpuestos buildTotalTaxes(final List<OrderDetail> orderDetails) {

        final TotalImpuestos totalImpuestos = new TotalImpuestos();

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

    public static List<Frases> buildPhrases(final FelInformation felInformation, final InvoiceType invoiceType) {
        
        final GeneratorInformation generator = getInvoiceGeneratorByType(felInformation, invoiceType);

        final List<Frases> list = new ArrayList<>();

        generator.getPhrases().forEach(p -> {

            Frases frases = new Frases();
            frases.setCodigoEscenario(p.getScenario());
            frases.setTipoFrase(p.getType());

            list.add(frases);
        });

        return list;
    }

    public static DatosReceptor buildDatosReceptor(final String taxId, 
                                                   final String taxIdType, 
                                                   final String name, 
                                                   final String email) {

        final DatosReceptor datosReceptor = new DatosReceptor();
        datosReceptor.setIDReceptor(taxId);
        datosReceptor.setTipoEspecial(taxIdType);
        datosReceptor.setCorreoReceptor(email);
        datosReceptor.setCodigoPostal("0000");
        datosReceptor.setDepartamento("Ciudad");
        datosReceptor.setDireccion("Ciudad");
        datosReceptor.setMunicipio("Ciudad");
        datosReceptor.setNombreReceptor(name);
        datosReceptor.setPais("GT");

        return datosReceptor;
    }

    public static Adendas buildAdendaPropina(final long orderId, final double tipAmount, final List<OrderDetail> orderDetails) {

        final double totalPlusTip = getTotal(orderDetails) + tipAmount;

        final Adendas adendas = new Adendas();
        adendas.setAdenda(TIP_ENTRY_NAME, String.format("%.2f", tipAmount));
        adendas.setAdenda(TOTAL_INVOICE_PLUS_TIP_ENTRY_NAME, String.format("%.2f", totalPlusTip));
        adendas.setAdenda(ORDER_ID_ENTRY_NAME, Long.toString(orderId));

        return adendas;
    }

    // -------------------------------------------------------------------------------------------------------------------

    private static Double getTotal(final List<OrderDetail> orderDetails) {

        return orderDetails.stream()
            .mapToDouble(detail -> (detail.getQuantity() * detail.getUnitPrice()) - detail.getDiscountAmount())
            .sum();
    }

    private static ImpuestosDetalle buildItemTaxDetail(final double itemTotal) {

        final double valueWithoutTax = itemTotal / 1.12;
        final ImpuestosDetalle detalle = new ImpuestosDetalle();
        detalle.setCodigoUnidadGravable(1);
        detalle.setMontoGravable(valueWithoutTax);
        detalle.setMontoImpuesto(itemTotal - valueWithoutTax);
        detalle.setNombreCorto(TAX_NAME);

        return detalle;
    }

    private static String generateDocumentDate() {

        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'-06:00'", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("America/Guatemala"));

        return sdf.format(new Date());
    }

    private static GeneratorInformation getInvoiceGeneratorByType(final FelInformation felInformation, final InvoiceType invoiceType) {

        final Generator generator = felInformation.getGenerator();

        return InvoiceType.BAR == invoiceType ? generator.getBarSubscription()
            : generator.getRestaurantSubscription();
    }
}
