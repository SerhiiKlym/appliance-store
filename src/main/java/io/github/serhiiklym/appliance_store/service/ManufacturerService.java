package io.github.serhiiklym.appliance_store.service;

import io.github.serhiiklym.appliance_store.model.Manufacturer;

import java.util.List;

public interface ManufacturerService {

    List<Manufacturer> list();

    Manufacturer getByIdOrThrow(Long id);

    Manufacturer create(String name);

    Manufacturer update(Long id, String name);

    void deleteManufacturer(Long id);

}
