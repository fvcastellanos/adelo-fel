package net.cavitos.aldelo.fel.domain.fel;

import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class GeneratorInformation {

    private String subscriptionType;
    private String taxId;
    private int code;
    private String name;
    private String email;
    private String country;
    private String city;
    private String state;
    private String address;
    private String postalCode;
    private String companyName;
    private List<Phrase> phrases;    
    private ApiInformation apiInformation;
}
