package io.github.serhiiklym.appliance_store.service.impl.util;

import io.github.serhiiklym.appliance_store.model.Appliance;

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
}
