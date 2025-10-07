package io.github.serhiiklym.appliance_store.service.impl;

import io.github.serhiiklym.appliance_store.error.DuplicateApplianceNameException;
import io.github.serhiiklym.appliance_store.error.NotFoundException;
import io.github.serhiiklym.appliance_store.model.Appliance;
import io.github.serhiiklym.appliance_store.model.Category;
import io.github.serhiiklym.appliance_store.model.Manufacturer;
import io.github.serhiiklym.appliance_store.model.PowerType;
import io.github.serhiiklym.appliance_store.repository.ApplianceRepository;
import io.github.serhiiklym.appliance_store.repository.ManufacturerRepository;
import io.github.serhiiklym.appliance_store.service.ApplianceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import static io.github.serhiiklym.appliance_store.repository.util.RepositoryUtilities.trimInputString;

@Slf4j
@Service
public class ApplianceServiceImpl implements ApplianceService {

    private final ApplianceRepository applianceRepository;
    //private final ManufacturerRepository manufacturerRepository;

    @Autowired
    public ApplianceServiceImpl(ApplianceRepository applianceRepository, ManufacturerRepository manufacturerRepository) {
        this.applianceRepository = applianceRepository;
        //this.manufacturerRepository = manufacturerRepository;
        log.debug("Initialized {} with repo={} and repo={}", getClass().getSimpleName(),
                applianceRepository.getClass().getSimpleName(), manufacturerRepository.getClass().getSimpleName());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Appliance> list() {
        log.debug("List of appliances");
        List<Appliance> result = applianceRepository.findAll();
        log.debug("List Method…: count={}", result.size());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Appliance getByIdOrThrow(Long id) {
        log.debug("Fetching Appliance ID={}", id);
        return applianceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Appliance not found with ID: " + id));
    }

    @Override
    @Transactional
    public Appliance create(String name, Category cat, String model, Manufacturer manufacturer, PowerType powerType,
                            String props, String descr, Integer power, BigDecimal price) {
        var trimmedName = trimInputString(name);

        if (applianceRepository.existsByManufacturerIdAndNameIgnoreCase(manufacturer.getId(), trimmedName)) {
            throw new DuplicateApplianceNameException(
                    String.format(Locale.ROOT, "Appliance name '%s' already exists", trimmedName)
            );
        }
//        Manufacturer manufacturer = manufacturerRepository.findById(manufacturerId)
//                .orElseThrow(() -> new NotFoundException("Creating appliance failed: Manufacturer not found with ID: " + manufacturerId));


        var trimmedModel = trimInputString(model);
        var trimmedProps = trimInputString(props);
        var trimmedDescr = trimInputString(descr);

        log.debug("Creating an Appliance with name={}, category={}, model={}, brand={}, power type={}, props={}," +
                        " description={}, power={}, price={}", trimmedName, cat, trimmedModel, manufacturer, powerType, trimmedProps,
                trimmedDescr, power, price);
        Appliance a = new Appliance();
        a.setName(trimmedName);
        a.setCategory(cat);
        a.setModel(trimmedModel);
        a.setManufacturer(manufacturer);
        a.setPowerType(powerType);
        a.setCharacteristic(trimmedProps);
        a.setDescription(trimmedDescr);
        a.setPower(power);
        a.setPrice(price);

        try {
            Appliance saved = applianceRepository.saveAndFlush(a);
            log.info("Created appliance id={}, name={}, brand={}", saved.getId(), saved.getName(), saved.getManufacturer().getName());
            return saved;
        } catch (DataIntegrityViolationException e) { // TODO - for now it's way too broad catch, refactor
            log.warn("DataIntegrityViolationException on create name={}", trimmedName, e);
            throw new RuntimeException(
                    String.format(Locale.ROOT, "Appliance creation RuntimeException", e)
            );
        }
    }

    @Override
    public Appliance update(String model, String props, String descr, BigDecimal price) {
        return null;
    }

    @Override
    public void deleteAppliance(Long id) {

    }
}
