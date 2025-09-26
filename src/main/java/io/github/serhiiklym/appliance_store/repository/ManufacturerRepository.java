package io.github.serhiiklym.appliance_store.repository;

import io.github.serhiiklym.appliance_store.model.Manufacturer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManufacturerRepository extends JpaRepository<Manufacturer, Long> {
}