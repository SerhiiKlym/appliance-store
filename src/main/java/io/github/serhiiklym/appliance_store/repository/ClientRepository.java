package io.github.serhiiklym.appliance_store.repository;

import io.github.serhiiklym.appliance_store.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
}
