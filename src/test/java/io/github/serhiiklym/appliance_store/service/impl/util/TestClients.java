package io.github.serhiiklym.appliance_store.service.impl.util;

import io.github.serhiiklym.appliance_store.model.Client;
import io.github.serhiiklym.appliance_store.repository.ClientRepository;

public final class TestClients {
    private TestClients() {}


    public static Client client(Long id) {
        Client c = new Client();
        c.setId(id);
        c.setName("Test Client " + id);
        return c;
    }

    /** Build a valid Client that passes Bean Validation, not persisted. */
    public static Client makeValidClient(String name) {
        Client c = new Client();
        c.setName(name);
        c.setEmail(name.toLowerCase().replaceAll("\\s+", "") + "@example.com");
        // long-ish string to satisfy @NotBlank/@Size; any dummy hash-like string is fine
        c.setPassword("$2a$10$aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        // satisfy @NotBlank/@Pattern if present
        c.setCard("4242424242424242");
        return c;
    }

    /** Convenience: build + persist using the repository passed in. */
    public static Client persistClientAllFields(ClientRepository repo, String name) {
        return repo.saveAndFlush(makeValidClient(name));
    }

}

