package io.github.serhiiklym.appliance_store.repository;

import io.github.serhiiklym.appliance_store.model.Appliance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplianceRepository extends JpaRepository<Appliance, Long> {
}
