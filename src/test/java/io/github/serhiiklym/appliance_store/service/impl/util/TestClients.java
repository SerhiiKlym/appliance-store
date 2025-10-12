package io.github.serhiiklym.appliance_store.service.impl.util;

import io.github.serhiiklym.appliance_store.model.Client;

public final class TestClients {
    private TestClients() {}

    public static Client client(Long id) {
        Client c = new Client();
        c.setId(id);
        c.setName("Test Client " + id);
        return c;
    }
}

