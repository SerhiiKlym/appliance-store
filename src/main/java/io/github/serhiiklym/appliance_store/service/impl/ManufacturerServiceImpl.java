package io.github.serhiiklym.appliance_store.service.impl;

import io.github.serhiiklym.appliance_store.error.ConflictException;
import io.github.serhiiklym.appliance_store.error.DuplicateManufacturerNameException;
import io.github.serhiiklym.appliance_store.error.NotFoundException;
import io.github.serhiiklym.appliance_store.model.Manufacturer;
import io.github.serhiiklym.appliance_store.repository.ManufacturerRepository;
import io.github.serhiiklym.appliance_store.service.ManufacturerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Slf4j
@Service
public class ManufacturerServiceImpl implements ManufacturerService {

    private final ManufacturerRepository manufacturerRepository;

    @Autowired
    public ManufacturerServiceImpl(ManufacturerRepository manufacturerRepository) {
        this.manufacturerRepository = manufacturerRepository;
        log.debug("Initialized {} with repo={}", getClass().getSimpleName(), manufacturerRepository.getClass().getSimpleName());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Manufacturer> list() {
        log.debug("List manufacturers");
        List<Manufacturer> result = manufacturerRepository.findAll();
        log.debug("List manufacturers: count={}", result.size());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Manufacturer getByIdOrThrow(Long id) {
        log.debug("Fetching Manufacturer ID={}", id);
        return manufacturerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Manufacturer not found with ID: " + id));
    }

    @Override
    @Transactional
    public Manufacturer create(String name) {
        var trimmedName = trimInputString(name);
        log.debug("Creating a Manufacturer with name={}", trimmedName);
        Manufacturer m = new Manufacturer();
        m.setName(trimmedName);

        try {
            Manufacturer saved = manufacturerRepository.saveAndFlush(m);
            log.info("Created manufacturer id={} name={}", saved.getId(), saved.getName());
            return saved;
        } catch (DataIntegrityViolationException e) {
            log.warn("Duplicate manufacturer on create name={}", trimmedName, e);
            throw new DuplicateManufacturerNameException(
                    String.format(Locale.ROOT, "Manufacturer name '%s' already exists", trimmedName)
            );
        }
    }

    @Override
    @Transactional
    public Manufacturer update(Long id, String name) {
        var trimmedName = trimInputString(name);
        log.debug("Updating Manufacturer by ID={}, with new name={}", id, trimmedName);
        Manufacturer manufacturer = manufacturerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Manufacturer not found with ID: " + id));

        manufacturer.setName(trimmedName);

        try {
            Manufacturer saved = manufacturerRepository.saveAndFlush(manufacturer);
            log.info("Updated manufacturer id={} name={}", saved.getId(), saved.getName());
            return saved;
        } catch (DataIntegrityViolationException e) {
            log.warn("Duplicate manufacturer on update name={}", trimmedName, e);
            throw new DuplicateManufacturerNameException(
                    String.format(Locale.ROOT, "Manufacturer name '%s' already exists", trimmedName)
            );
        }
    }

    @Override
    @Transactional
    public void deleteManufacturer(Long id) {
        log.info("Deleting Manufacturer with ID={}", id);
        try {
            manufacturerRepository.deleteById(id);
            log.info("Deleted manufacturer id={}", id);
        } catch (EmptyResultDataAccessException e) { // good exception?
            log.warn("No Manufacturer id={} to delete", id, e);
            throw new NotFoundException("Manufacturer not found with ID: " + id); // 404
        } catch (DataIntegrityViolationException e) {
            log.warn("Cannot delete Manufacturer due to FK constraint id={}", id, e);
            throw new ConflictException("Cannot delete manufacturer with existing appliances. ID " + id); // 409- FK collision
        }
    }

    // -- helpers --
    private static String trimInputString(String input) {
        return Objects.requireNonNull(input, "name must not be null").trim();
    }
}