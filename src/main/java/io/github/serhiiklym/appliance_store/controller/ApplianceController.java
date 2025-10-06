package io.github.serhiiklym.appliance_store.controller;

import io.github.serhiiklym.appliance_store.controller.dto.ApplianceForm;
import io.github.serhiiklym.appliance_store.error.DuplicateApplianceNameException;
import io.github.serhiiklym.appliance_store.model.Category;
import io.github.serhiiklym.appliance_store.model.Manufacturer;
import io.github.serhiiklym.appliance_store.model.PowerType;
import io.github.serhiiklym.appliance_store.service.ApplianceService;
import io.github.serhiiklym.appliance_store.service.ManufacturerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/appliances")
public class ApplianceController {

    private final ApplianceService service;
    private final ManufacturerService manufacturerService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("appliances", service.list());
        return "/appliance/appliances";
    }

    @GetMapping("/add")
    public String showCreateForm(Model model) {
        model.addAttribute("form", new ApplianceForm());
        return "/appliance/newAppliance";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("form") ApplianceForm form,
                         BindingResult binding,
                         RedirectAttributes ra){
        if (binding.hasErrors()) return "/appliance/newAppliance";

        try {
            service.create(form.getName(), form.getCategory(), form.getModel(), form.getManufacturer(),
                    form.getPowerType(), form.getCharacteristic(), form.getDescription(),
                    form.getPower(), form.getPrice());
        } catch (DuplicateApplianceNameException | DataIntegrityViolationException ex) {
            binding.rejectValue("name", "appliance.name.duplicate",
                    new Object[]{form.getName()}, null);
            return "/appliance/newAppliance"; // stay on page, show inline error
        }
        ra.addFlashAttribute("flashSuccess", "Created: " + form.getName());
        return "redirect:/appliances";
    }


    // Run before every handler method in this controller and adds data to the model (select dropdowns)
    @ModelAttribute("categories")
    public Category[] categories() {
        return Category.values();
    }

    @ModelAttribute("powerTypes")
    public PowerType[] powerTypes() {
        return PowerType.values();
    }

    @ModelAttribute("manufacturers")
    public List<Manufacturer> manufacturers() {
        return manufacturerService.list();
    }

}

