package io.github.serhiiklym.appliance_store.repository;

import io.github.serhiiklym.appliance_store.model.Appliance;
import io.github.serhiiklym.appliance_store.model.Manufacturer;
import io.github.serhiiklym.appliance_store.model.Category;
import io.github.serhiiklym.appliance_store.model.PowerType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Web MVC slice tests for ApplianceController.
 * Loads only MVC + the controller via @WebMvcTest; replaces collaborators with @MockitoBean mocks.
 * Security filters disabled (@AutoConfigureMockMvc(addFilters = false)) to focus on views, model, redirects, and validation.
 * Does NOT test security rules or persistence.
 */

@DataJpaTest
class ApplianceRepositoryTest {

    @Autowired TestEntityManager em;
    @Autowired ApplianceRepository repo;

    @Test
    void existsByManufacturerIdAndNameIgnoreCase_behavesAsExpected() {
        // given a manufacturer
        Manufacturer m1 = new Manufacturer();
        m1.setName("BrandA");
        m1 = em.persistFlushFind(m1);

        Manufacturer m2 = new Manufacturer();
        m2.setName("BrandB");
        m2 = em.persistFlushFind(m2);

        // and an appliance under m1 with name 'Blender'
        Appliance a = new Appliance();
        a.setName("Blender");
        // pick the first enum constants to avoid coupling to specific names
        a.setCategory(Category.values()[0]);
        a.setModel("BL-100");
        a.setManufacturer(m1);
        a.setPowerType(PowerType.values()[0]);
        a.setCharacteristic("500W; steel");
        a.setDescription("A nice blender");
        a.setPower(500);
        a.setPrice(new BigDecimal("149.99"));
        em.persist(a);
        em.flush();

        // when/then
        assertThat(repo.existsByManufacturerIdAndNameIgnoreCase(m1.getId(), "blender")).isTrue();
        assertThat(repo.existsByManufacturerIdAndNameIgnoreCase(m1.getId(), "BLENDER")).isTrue(); // case-insensitive
        assertThat(repo.existsByManufacturerIdAndNameIgnoreCase(m1.getId(), "Mixer")).isFalse(); // different name
        assertThat(repo.existsByManufacturerIdAndNameIgnoreCase(m2.getId(), "Blender")).isFalse(); // different brand
    }
}
