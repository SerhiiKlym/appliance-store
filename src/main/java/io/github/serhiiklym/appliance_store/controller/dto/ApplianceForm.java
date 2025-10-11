package io.github.serhiiklym.appliance_store.controller.dto;

import io.github.serhiiklym.appliance_store.model.Category;
import io.github.serhiiklym.appliance_store.model.Manufacturer;
import io.github.serhiiklym.appliance_store.model.PowerType;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Minimal form object for create Appliance.
 */
@Data
@NoArgsConstructor
public class ApplianceForm {

    @NotBlank(message = "{appliance.name.required}")
    @Size(max = 30, message = "{appliance.name.max}")
    private String name;

    @NotNull(message = "{appliance.manufacturer.id.required}")
    private Manufacturer manufacturer; // select

    @NotNull(message = "{appliance.category.required}")
    private Category category; // select

    @Size(max = 30, message = "{appliance.model.max}")
    private String model;

    @NotNull(message = "{appliance.power.type.required}")
    private PowerType powerType; // select

    @Size(max = 100, message = "{appliance.characteristic.max}")
    private String characteristic;

    @Size(max = 300, message = "{appliance.description.max}")
    private String description;

    @PositiveOrZero(message = "{appliance.power.nonNegative}")
    @NotNull(message = "{appliance.power.required}")
    private Integer power;

    @NotNull(message = "{appliance.price.required}")
    @Positive(message = "{appliance.price.positive}")
    @Digits(integer = 9, fraction = 2, message = "{appliance.price.digits}")
    private BigDecimal price;

}
