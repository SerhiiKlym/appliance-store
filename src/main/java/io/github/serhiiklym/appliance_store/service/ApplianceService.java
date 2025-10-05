package io.github.serhiiklym.appliance_store.service;

import io.github.serhiiklym.appliance_store.model.Appliance;

import java.util.List;

public interface ApplianceService {

    List<Appliance> list();

    Appliance getByIdOrThrow(Long id);

    Appliance create(String name);

    Appliance update(Long id, String name);

    void deleteAppliance(Long id);
}
