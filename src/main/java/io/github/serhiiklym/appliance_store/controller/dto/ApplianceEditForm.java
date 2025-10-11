package io.github.serhiiklym.appliance_store.controller.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Minimal form object for update Appliance.
 */
@Data
public class ApplianceEditForm {

    @Size(max = 30, message = "{appliance.model.max}")
    private String model;

    @Size(max = 300, message = "{appliance.description.max}")
    private String description;

    @Size(max = 100, message = "{appliance.characteristic.max}")
    private String characteristic;

    @NotNull(message = "{appliance.price.required}")
    @Positive(message = "{appliance.price.positive}")
    @Digits(integer = 9, fraction = 2, message = "{appliance.price.digits}")
    private BigDecimal price;

}
