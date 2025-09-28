package io.github.serhiiklym.appliance_store.service.impl;

import io.github.serhiiklym.appliance_store.model.Manufacturer;

import java.util.List;

public class ManufacturerSamples {

    public static Manufacturer apple() {
        return m(4L, "Apple");
    }

    public static Manufacturer amd() {
        return m(7L, "AMD");
    }

    public static Manufacturer samsung() {
        return m(1L, "Samsung");
    }


    /** Generic factory to keep call sites concise. */
    public static Manufacturer m(long id, String name) {
        return new Manufacturer(id, name);
    }

    // --- Zero-One-Many baselines

    public static List<Manufacturer> empty() {
        return List.of();
    }

    public static List<Manufacturer> one() {
        return List.of(apple());
    }

    public static List<Manufacturer> many() {
        return List.of(samsung(), apple(), amd());
    }

}


