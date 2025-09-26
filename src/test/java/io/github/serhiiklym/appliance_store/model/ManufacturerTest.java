package io.github.serhiiklym.appliance_store.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;

import static io.github.serhiiklym.appliance_store.model.TestConstants.*;
import static io.github.serhiiklym.appliance_store.model.TestConstants.Manufacturer.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ManufacturerTest {
    private static List<Field> allFields;
    private static List<Constructor<?>> allConstructors;

    @BeforeAll
    static void setup() throws ClassNotFoundException {
        final Class<?> clazz = Class.forName(MANUFACTURER_TYPE);
        allFields = Arrays.asList(clazz.getDeclaredFields());
        allConstructors = Arrays.asList(clazz.getConstructors());
    }

    /* Tests for CONSTRUCTORS */
    @Test
    @DisplayName("Count constructors")
    void checkCountConstructors() {
        assertEquals(CLASS_COUNT_CONSTRUCTORS, allConstructors.size());
    }

    @Test
    @DisplayName("Modifiers constructors can be public")
    void checkModifiersConstructors() {
        boolean actual = allConstructors.stream()
                .allMatch(c -> Modifier.isPublic(c.getModifiers()));
        assertTrue(actual);
    }

    @Test
    @DisplayName("Manufacturer has to default constructor")
    void checkDefaultConstructor() {
        long count = allConstructors.stream()
                .filter(c -> c.getParameterCount() == 0)
                .count();
        assertEquals(1, count);
    }

    @Test
    @DisplayName("Manufacturer has constructor with 2 parameter")
    void checkConstructorWithParameter() {
        long count = allConstructors.stream()
                .filter(c -> c.getParameterCount() == PARAMETERS_IN_CONSTRUCTOR_WITH_PARAMETERS)
                .count();
        assertEquals(1, count);
    }

    @Test
    @DisplayName("Check parameter type in constructor with parameter")
    void checkParameterTypeForConstructorWithParameter() {
        final Constructor<?> constructor = allConstructors.stream()
                .filter(c -> c.getParameterCount() == PARAMETERS_IN_CONSTRUCTOR_WITH_PARAMETERS)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No constructor with two parameters"));

        final List<Parameter> parameters = Arrays.asList(constructor.getParameters());


        parameters.stream()
                .filter(p -> p.getType().getTypeName().equals(LONG_TYPE))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No parameter with type " + LONG_TYPE));

        parameters.stream()
                .filter(p -> p.getType().getTypeName().equals(STRING_TYPE))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No parameter with type " + STRING_TYPE));

    }

    /* Tests for FIELDS */
    @Test
    @DisplayName("Count fields")
    void checkCountFields() {
        assertEquals(CLASS_COUNT_FIELDS, allFields.size());
    }

    @Test
    @DisplayName(CLASS_NAME + " has field with name " + FIELD_ID)
    void checkFieldNameId() {
        allFields.stream()
                .filter(f -> f.getName().equals(FIELD_ID))
                .findFirst().orElseThrow(() -> new RuntimeException("Field " + FIELD_ID + " not found"));
    }

    @Test
    @DisplayName(CLASS_NAME + " has field with name " + FIELD_NAME)
    void checkFieldNameName() {
        allFields.stream()
                .filter(f -> f.getName().equals(FIELD_NAME))
                .findFirst().orElseThrow(() -> new RuntimeException("Field " + FIELD_NAME + " not found"));
    }

    @Test
    @DisplayName("Modifiers fields can be private")
    void checkModifiersFields() {
        long count = allFields.stream()
                .filter(f -> Modifier.isPrivate(f.getModifiers()))
                .count();

        assertEquals(CLASS_COUNT_FIELDS, count);
    }

    @Test
    @DisplayName("Check " + FIELD_ID + " field type")
    void checkIdFieldType() {
        final long countLong = allFields.stream()
                .filter(f -> f.getType().getTypeName().equals(LONG_TYPE)
                        & f.getName().equals(FIELD_ID))
                .count();
        assertEquals(1, countLong);
    }

    @Test
    @DisplayName("Check " + FIELD_NAME + " field type")
    void checkNameFieldType() {
        final long countLong = allFields.stream()
                .filter(f -> f.getType().getTypeName().equals(STRING_TYPE)
                        & f.getName().equals(FIELD_NAME))
                .count();
        assertEquals(1, countLong);
    }

}