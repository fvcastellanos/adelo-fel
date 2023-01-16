package net.cavitos.aldelo.fel.domain.fel;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Generator {
    
    private GeneratorInformation barSubscription;
    private GeneratorInformation restaurantSubscription;
}
