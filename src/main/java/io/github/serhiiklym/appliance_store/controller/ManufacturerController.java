package io.github.serhiiklym.appliance_store.controller;

import io.github.serhiiklym.appliance_store.controller.dto.ManufacturerForm;
import io.github.serhiiklym.appliance_store.error.ConflictException;
import io.github.serhiiklym.appliance_store.error.DuplicateManufacturerNameException;
import io.github.serhiiklym.appliance_store.error.NotFoundException;
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

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/manufacturers")
public class ManufacturerController extends BaseController {

    private final ManufacturerService service;

    @GetMapping
    public String list(Model model, @ModelAttribute("flashSuccess") String flashSuccess) {
        model.addAttribute("manufacturers", service.list());
        return "manufacturer/manufacturers";
    }

    // GET /manufacturers/new -> create form
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("form", new ManufacturerForm());
        return "manufacturer/newManufacturer";
    }

    // POST /manufacturers -> create then redirect
    @PostMapping
    public String create(@Valid @ModelAttribute("form") ManufacturerForm form,
                         BindingResult binding,
                         RedirectAttributes ra) {
        if (binding.hasErrors()) return "manufacturer/newManufacturer";

        try {
            service.create(form.getName().trim());
        } catch (DuplicateManufacturerNameException | DataIntegrityViolationException ex) {
            flashErrorCode(ra, "manufacturer.name.duplicate", form.getName());
            return "manufacturer/newManufacturer"; // stay on page, show inline error
        }
        flashSuccess(ra, "manufacturer.created");
        return "redirect:/manufacturers";
    }

    // GET /manufacturers/{id}/edit -> edit form
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        var m = service.getByIdOrThrow(id);
        var form = new ManufacturerForm();
        form.setName(m.getName());
        model.addAttribute("id", id);
        model.addAttribute("form", form);
        return "manufacturer/editManufacturer";
    }

    // POST /manufacturers/{id} -> update then redirect
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("form") ManufacturerForm form,
                         BindingResult errors,
                         RedirectAttributes ra) {
        if (errors.hasErrors()) return "manufacturer/editManufacturer";
        try {
            service.update(id, form.getName());
            flashSuccess(ra, "manufacturer.updated");
            return "redirect:/manufacturers";
        } catch (ConflictException e) {
            flashErrorText(ra, "general.error.manufacturer");
            return "manufacturer/editManufacturer";
        }
    }

    // POST /manufacturers/{id}/delete -> delete then redirect
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            service.deleteManufacturer(id);
            log.info("Deleted manufacturer id={}", id);
            flashSuccess(ra, "manufacturer.deleted");
            return "redirect:/manufacturers";
        } catch (DataIntegrityViolationException ex) {
            // there are appliances linked to this brand
            ra.addAttribute("id", id); // <-- lets {id} expand in the redirect URL
            flashErrorText(ra, "manufacturer.delete.constraint");
            return "redirect:/manufacturers/{id}/edit";
        } catch (NotFoundException ex) {
            flashErrorText(ra, "manufacturer.notfound");
            return "redirect:/manufacturers";
        }
    }
}
