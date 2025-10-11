package io.github.serhiiklym.appliance_store.service.impl;

import io.github.serhiiklym.appliance_store.error.DuplicateApplianceNameException;
import io.github.serhiiklym.appliance_store.error.NotFoundException;
import io.github.serhiiklym.appliance_store.model.Appliance;
import io.github.serhiiklym.appliance_store.model.Manufacturer;
import io.github.serhiiklym.appliance_store.repository.ApplianceRepository;
import io.github.serhiiklym.appliance_store.repository.ManufacturerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.dao.EmptyResultDataAccessException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplianceServiceImplTest {

    @Mock ApplianceRepository applianceRepository;
    @Mock ManufacturerRepository manufacturerRepository;

    @InjectMocks ApplianceServiceImpl service; // concrete class under test

    @Test
    @DisplayName("list(): delegates to repository and returns all")
    void list_returnsAll() {
        when(applianceRepository.findAll()).thenReturn(List.of(new Appliance(), new Appliance()));
        assertThat(service.list()).hasSize(2);
        verify(applianceRepository, times(1)).findAll();
        verifyNoMoreInteractions(applianceRepository);
    }

    @Test
    @DisplayName("getByIdOrThrow(): returns entity when found")
    void getByIdOrThrow_found() {
        Appliance a = new Appliance();
        a.setId(42L);
        when(applianceRepository.findById(42L)).thenReturn(Optional.of(a));

        var found = service.getByIdOrThrow(42L);

        assertThat(found).isSameAs(a);
        verify(applianceRepository).findById(42L);
    }

    @Test
    @DisplayName("getByIdOrThrow(): throws NotFound when missing")
    void getByIdOrThrow_notFound() {
        when(applianceRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getByIdOrThrow(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("create(): happy path saves and returns the entity")
    void create_happyPath() {
        Manufacturer m = new Manufacturer();
        m.setId(1L);
        // duplicate check must be false
        when(applianceRepository.existsByManufacturerIdAndNameIgnoreCase(eq(1L), eq("Blender"))).thenReturn(false);

        // accept either save() or saveAndFlush() depending on impl; stub both safely
        Answer<Appliance> saver = inv -> {
            Appliance saved = inv.getArgument(0, Appliance.class);
            saved.setId(1L);
            return saved;
        };

        when(applianceRepository.saveAndFlush(any(Appliance.class))).thenAnswer(saver);

        var created = service.create(
                "  Blender  ", /*cat*/ null, "B123", m, /*powerType*/ null,
                "specs", "desc", 500, new BigDecimal("149.99")
        );

        assertThat(created.getId()).isEqualTo(1L);
        assertThat(created.getName()).isEqualTo("Blender"); // trimmed
        assertThat(created.getManufacturer()).isSameAs(m);
        verify(applianceRepository).existsByManufacturerIdAndNameIgnoreCase(1L, "Blender");
        // one of these two will be called by your impl:
        verify(applianceRepository, atMostOnce()).save(any(Appliance.class));
        verify(applianceRepository, atMostOnce()).saveAndFlush(any(Appliance.class));
    }

    @Test
    @DisplayName("create(): duplicate name under same manufacturer -> DuplicateApplianceNameException")
    void create_duplicateName() {
        Manufacturer m = new Manufacturer();
        m.setId(7L);
        when(applianceRepository.existsByManufacturerIdAndNameIgnoreCase(7L, "Blender")).thenReturn(true);

        assertThatThrownBy(() ->
                service.create("Blender", null, "B123", m, null, "specs", "desc", 500, new BigDecimal("149.99"))
        ).isInstanceOf(DuplicateApplianceNameException.class);

        verify(applianceRepository).existsByManufacturerIdAndNameIgnoreCase(7L, "Blender");
        verifyNoMoreInteractions(applianceRepository);
    }

    @Test
    @DisplayName("update(): updates mutable fields and saves")
    void update_happyPath() {
        Appliance existing = new Appliance();
        existing.setId(10L);
        existing.setModel("OLD");
        existing.setCharacteristic("OLD");
        existing.setDescription("OLD");
        existing.setPrice(new BigDecimal("1.00"));

        when(applianceRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(applianceRepository.saveAndFlush(any(Appliance.class))).thenAnswer(inv -> inv.getArgument(0));

        var updated = service.update(10L, "NEW-MOD", "NEW-PROPS", "NEW-DESC", new BigDecimal("2.50"));

        assertThat(updated.getModel()).isEqualTo("NEW-MOD");
        assertThat(updated.getCharacteristic()).isEqualTo("NEW-PROPS");
        assertThat(updated.getDescription()).isEqualTo("NEW-DESC");
        assertThat(updated.getPrice()).isEqualByComparingTo("2.50");

        verify(applianceRepository).findById(10L);
        verify(applianceRepository).saveAndFlush(existing);
    }

    @Test
    @DisplayName("update(): throws NotFound when id missing")
    void update_notFound() {
        when(applianceRepository.findById(111L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.update(111L, "M", "P", "D", new BigDecimal("3.00")))
                .isInstanceOf(NotFoundException.class);
        verify(applianceRepository).findById(111L);
        verify(applianceRepository, never()).saveAndFlush(any());
    }

    @Test
    @DisplayName("deleteAppliance(): deletes and flushes")
    void delete_success() {
        // no stubbing needed for voids; verify interaction
        service.deleteAppliance(5L);
        verify(applianceRepository).deleteById(5L);
        verify(applianceRepository).flush();
    }

    @Test
    @DisplayName("deleteAppliance(): translates EmptyResultDataAccessException to NotFoundException")
    void delete_notFound() {
        doThrow(new EmptyResultDataAccessException(1)).when(applianceRepository).deleteById(123L);
        assertThatThrownBy(() -> service.deleteAppliance(123L))
                .isInstanceOf(NotFoundException.class);
        verify(applianceRepository).deleteById(123L);
        // flush not called when delete fails
        verify(applianceRepository, never()).flush();
    }
}