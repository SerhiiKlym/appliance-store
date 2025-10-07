package io.github.serhiiklym.appliance_store.service;

import io.github.serhiiklym.appliance_store.model.Appliance;
import io.github.serhiiklym.appliance_store.model.Category;
import io.github.serhiiklym.appliance_store.model.Manufacturer;
import io.github.serhiiklym.appliance_store.model.PowerType;

import java.math.BigDecimal;
import java.util.List;

public interface ApplianceService {

    List<Appliance> list();

    Appliance getByIdOrThrow(Long id);

    Appliance create(String name, Category cat, String model, Manufacturer manufacturer, PowerType powerType,
                     String props, String descr, Integer power, BigDecimal price);

    Appliance update(String model, String props, String descr, BigDecimal price);

    void deleteAppliance(Long id);
}
