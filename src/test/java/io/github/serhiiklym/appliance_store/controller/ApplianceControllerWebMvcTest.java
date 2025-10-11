package io.github.serhiiklym.appliance_store.controller;

import io.github.serhiiklym.appliance_store.error.DuplicateApplianceNameException;
import io.github.serhiiklym.appliance_store.error.NotFoundException;
import io.github.serhiiklym.appliance_store.model.Appliance;
import io.github.serhiiklym.appliance_store.model.Manufacturer;
import io.github.serhiiklym.appliance_store.service.ApplianceService;
import io.github.serhiiklym.appliance_store.service.ManufacturerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Assumes:
 *  - @Controller class ApplianceController mapped at "/appliances"
 *  - list(): model.addAttribute("appliances", service.list()); returns "appliance/appliances"
 *  - GET "/new" -> "appliance/newAppliance"
 *  - POST "/" (create) with @ModelAttribute("form") -> redirect on success; on errors stays on "appliance/newAppliance"
 *  - GET "/{id}/edit" -> "appliance/editAppliance"
 *  - POST "/{id}" (update) -> redirect on success; stays on edit on errors
 *  - POST "/{id}/delete" (delete) -> redirect with flash
 */
@WebMvcTest(controllers = ApplianceController.class)
@AutoConfigureMockMvc(addFilters = false)
class ApplianceControllerWebMvcTest {

    @Autowired MockMvc mvc;

    @MockitoBean ApplianceService service;
    @MockitoBean ManufacturerService manufacturerService;

    @Test
    @DisplayName("GET /appliances: renders list view with appliances model")
    void list_rendersView() throws Exception {
        var a1 = new Appliance(); a1.setId(1L);
        var a2 = new Appliance(); a2.setId(2L);
        given(service.list()).willReturn(List.of(a1, a2));

        mvc.perform(get("/appliances"))
                .andExpect(status().isOk())
                .andExpect(view().name("appliance/appliances"))
                .andExpect(model().attributeExists("appliances"))
                .andExpect(model().attribute("appliances", hasSize(2)));
    }

    @Test
    @DisplayName("POST /appliances: valid create -> redirect with success flash")
    void create_valid_redirects() throws Exception {
        when(service.create(anyString(), any(), anyString(), any(Manufacturer.class), any(), anyString(),
                anyString(), anyInt(), any(BigDecimal.class)))
                .thenReturn(new Appliance());

        mvc.perform(post("/appliances")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        // minimal required fields (adjust param names to your form if needed)
                        .param("name", "Blender")
                        .param("category", "SMALL") // enum binder; adjust if your values differ
                        .param("model", "BL-100")
                        .param("manufacturer.id", "1")
                        .param("powerType", "ACCUMULATOR")
                        .param("characteristic", "500W; steel")
                        .param("description", "Nice one")
                        .param("power", "5000")
                        .param("price", "149.99")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/appliances"))
                .andExpect(flash().attributeExists("flashSuccess"));
    }

    @Test
    @DisplayName("POST /appliances: invalid form -> stays on page and shows errors")
    void create_invalid_showsErrors() throws Exception {
        // Force validation failure by missing required fields (e.g., name, price)
        mvc.perform(post("/appliances")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "")           // @NotBlank
                        .param("price", "-1")        // @Positive
                )
                .andExpect(status().isOk())
                .andExpect(view().name("appliance/newAppliance"))
                .andExpect(model().hasErrors());
    }

    @Test
    @DisplayName("POST /appliances: duplicate -> stays on page with field error")
    void create_duplicate_staysWithError() throws Exception {
        when(service.create(anyString(), any(), anyString(), any(Manufacturer.class), any(), anyString(),
                anyString(), anyInt(), any(BigDecimal.class)))
                .thenThrow(new DuplicateApplianceNameException("duplicate"));

        mvc.perform(post("/appliances")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "Blender")
                        .param("model", "BL-100")
                        .param("manufacturer.id", "1")
                        .param("price", "149.99")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("appliance/newAppliance"))
                .andExpect(model().hasErrors());
    }

    @Test
    @DisplayName("POST /appliances/{id}: valid update -> redirect with success flash")
    void update_valid_redirects() throws Exception {
        mvc.perform(post("/appliances/{id}", 10L)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("model", "NEW-MOD")
                        .param("characteristic", "NEW-PROPS")
                        .param("description", "NEW-DESC")
                        .param("price", "2.50")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/appliances"))
                .andExpect(flash().attributeExists("flashSuccess"));

        verify(service).update(eq(10L), eq("NEW-MOD"), eq("NEW-PROPS"), eq("NEW-DESC"), eq(new BigDecimal("2.50")));
    }

    @Test
    @DisplayName("POST /appliances/{id}/delete: success -> redirect with success flash")
    void delete_success() throws Exception {
        mvc.perform(post("/appliances/{id}/delete", 5L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/appliances"))
                .andExpect(flash().attributeExists("flashSuccess"));

        verify(service).deleteAppliance(5L);
    }

    @Test
    @DisplayName("POST /appliances/{id}/delete: not found -> redirect with error flash")
    void delete_notFound() throws Exception {
        doThrow(new NotFoundException("missing")).when(service).deleteAppliance(123L);

        mvc.perform(post("/appliances/{id}/delete", 123L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/appliances"))
                .andExpect(flash().attributeExists("flashError"));
    }
}
