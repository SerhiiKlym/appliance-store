package io.github.serhiiklym.appliance_store.service.impl.util;

import io.github.serhiiklym.appliance_store.model.Manufacturer;
import io.github.serhiiklym.appliance_store.repository.ManufacturerRepository;

public final class TestManufacturers {
    private TestManufacturers() {}

    /** Build a valid, non-persisted Manufacturer. Add more fields if your entity requires them. */
    public static Manufacturer makeValidManufacturer(String name) {
        Manufacturer m = new Manufacturer();
        m.setName(name);
        return m;
    }

    /** Persist a valid manufacturer using the repo provided by the test. */
    public static Manufacturer persistManufacturer(ManufacturerRepository repo, String name) {
        return repo.saveAndFlush(makeValidManufacturer(name));
    }
}
