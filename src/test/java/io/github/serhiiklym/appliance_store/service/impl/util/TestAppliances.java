package io.github.serhiiklym.appliance_store.service.impl.util;

import io.github.serhiiklym.appliance_store.model.Appliance;
import io.github.serhiiklym.appliance_store.model.Category;
import io.github.serhiiklym.appliance_store.model.Manufacturer;
import io.github.serhiiklym.appliance_store.model.PowerType;
import io.github.serhiiklym.appliance_store.repository.ApplianceRepository;
import io.github.serhiiklym.appliance_store.repository.ManufacturerRepository;

import java.math.BigDecimal;

public final class TestAppliances {
    private TestAppliances() {}

    public static Appliance appliance(Long id, BigDecimal price) {
        Appliance a = new Appliance();
        a.setId(id);
        a.setName("Appliance " + id);
        a.setPrice(price);
        return a;
    }
    /** Build a valid, non-persisted Appliance (fills all commonly @NotNull/@NotBlank fields). */
    public static Appliance makeValidAppliance(
            String name,
            String model,
            String price,
            Category category,
            PowerType powerType,
            Manufacturer manufacturer
    ) {
        Appliance a = new Appliance();
        a.setName(name);
        a.setModel(model);
        a.setCharacteristic("specs");
        a.setDescription("test description");
        a.setPower(100);
        a.setPrice(new BigDecimal(price));
        a.setCategory(category);
        a.setPowerType(powerType);
        a.setManufacturer(manufacturer);
        return a;
    }

    /** Convenience: create + persist manufacturer, then create + persist appliance. */
    public static Appliance persistApplianceAllFields(
            ApplianceRepository applianceRepo,
            ManufacturerRepository manufacturerRepo,
            String manufacturerName,
            String name,
            String model,
            String price,
            Category category,
            PowerType powerType
    ) {
        Manufacturer m = TestManufacturers.persistManufacturer(manufacturerRepo, manufacturerName);
        return applianceRepo.saveAndFlush(
                makeValidAppliance(name, model, price, category, powerType, m)
        );
    }
}
