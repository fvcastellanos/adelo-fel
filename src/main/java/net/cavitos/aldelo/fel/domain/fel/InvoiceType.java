package net.cavitos.aldelo.fel.domain.fel;

public enum InvoiceType {
    
    RESTAURANT("RESTAURANT"),
    BAR("BAR");

    private final String type;

    InvoiceType(final String type) {

        this.type = type;
    }

    public String value() {

        return type;
    }

    public static InvoiceType of(final String type) {

        return "BAR".equalsIgnoreCase(type) ? BAR
            : RESTAURANT;
    }

}
