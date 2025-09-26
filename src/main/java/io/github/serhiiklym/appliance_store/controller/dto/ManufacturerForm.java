package io.github.serhiiklym.appliance_store.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Minimal form object for create/update Manufacturer.
 */

//TODO add messages to props
@Data
@NoArgsConstructor
public class ManufacturerForm {

    @NotBlank(message = "{manufacturer.name.required}")
    @Size(max = 30, message = "{manufacturer.name.max}")
    private String name;

}
