package io.github.serhiiklym.appliance_store.service.impl;

import io.github.serhiiklym.appliance_store.repository.ManufacturerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManufacturerServiceImplTest {

    @Mock
    private ManufacturerRepository repository;

    @InjectMocks
    private ManufacturerServiceImpl service;

    @Test
    @DisplayName("list() should return all the manufacturers in it")
    void list_should_return_all_manufacturers_many() {
        var expected = ManufacturerSamples.many();
        when(repository.findAll()).thenReturn(expected);

        var actual = service.list();

        assertThat(actual).containsExactlyElementsOf(expected);
        verify(repository, times(1)).findAll();
        verifyNoMoreInteractions(repository);
    }

    //TODO implement tests before merging into master
    @Test
    void getByIdOrThrow() {
    }

    @Test
    void create() {
    }

    @Test
    void update() {
    }

    @Test
    void deleteManufacturer() {
    }
}