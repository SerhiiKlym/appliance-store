package io.github.serhiiklym.appliance_store.controller;

import io.github.serhiiklym.appliance_store.controller.dto.ApplianceEditForm;
import io.github.serhiiklym.appliance_store.controller.dto.ApplianceForm;
import io.github.serhiiklym.appliance_store.controller.dto.ManufacturerForm;
import io.github.serhiiklym.appliance_store.error.DuplicateApplianceNameException;
import io.github.serhiiklym.appliance_store.error.NotFoundException;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/appliances")
public class ApplianceController extends BaseController {

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
        return "appliance/newAppliance";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("form") ApplianceForm form,
                         BindingResult binding,
                         RedirectAttributes ra) {
        if (binding.hasErrors()) return "appliance/newAppliance";

        try {
            service.create(form.getName(), form.getCategory(), form.getModel(), form.getManufacturer(),
                    form.getPowerType(), form.getCharacteristic(), form.getDescription(),
                    form.getPower(), form.getPrice());
        } catch (DuplicateApplianceNameException | DataIntegrityViolationException ex) {
            flashErrorCode(ra, "appliance.name.duplicate", form.getName());
            return "appliance/newAppliance"; // stay on page, show inline error
        }
        flashSuccess(ra, "appliance.created");
        return "redirect:/appliances";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        var a = service.getByIdOrThrow(id);

        var form = new ApplianceForm();
        form.setModel(a.getModel());
        form.setCharacteristic(a.getCharacteristic());
        form.setDescription(a.getDescription());
        form.setPrice(a.getPrice());

        model.addAttribute("id", id);
        model.addAttribute("form", form);
        model.addAttribute("appliance", a); // for read-only display
        return "appliance/editAppliance";
    }

    @PostMapping("/{id}")
    public String update(
            @PathVariable Long id,
            @Valid @ModelAttribute("form") ApplianceEditForm form,
            BindingResult br,
            Model model,
            RedirectAttributes ra) {

        if (br.hasErrors()) {
            model.addAttribute("id", id);
            model.addAttribute("appliance", service.getByIdOrThrow(id));
            return "appliance/editAppliance";
        }

        // Update ONLY the editable fields
        service.update(id, form.getModel(), form.getCharacteristic(), form.getDescription(), form.getPrice());

        flashSuccess(ra, "appliance.updated");
        return "redirect:/appliances";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            service.deleteAppliance(id);
            log.info("Deleted Appliance with id={}", id);
            flashSuccess(ra, "appliance.deleted");
            return "redirect:/appliances";
        } catch (DataIntegrityViolationException ex) {
            flashErrorCode(ra, "appliance.delete.constraint");
            return "redirect:/appliance/{id}/edit";
        } catch (NotFoundException ex) {
            flashErrorText(ra, "appliance.notfound");
            return "redirect:/appliances";
        }
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

