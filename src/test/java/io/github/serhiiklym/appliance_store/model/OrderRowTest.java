package io.github.serhiiklym.appliance_store.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;

import static io.github.serhiiklym.appliance_store.model.TestConstants.*;
import static io.github.serhiiklym.appliance_store.model.TestConstants.OrderRow.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderRowTest {
    private static List<Field> allFields;
    private static List<Constructor<?>> allConstructors;

    @BeforeEach
    void setUp() throws ClassNotFoundException {
        Class<?> clazz = Class.forName(ORDER_ROW_TYPE);
        allFields = Arrays.asList(clazz.getDeclaredFields());
        allConstructors = Arrays.asList(clazz.getConstructors());
    }

    /*Tests for constructors*/
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
    @DisplayName("OrderRow has to default constructor")
    void checkDefaultConstructor() {
        long count = allConstructors.stream()
                .filter(c -> c.getParameterCount() == 0)
                .count();
        assertEquals(1, count);
    }

    @Test
    @DisplayName("OrderRow has to constructor with 2 parameter")
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
                .orElseThrow(() -> new RuntimeException("No constructor with " + PARAMETERS_IN_CONSTRUCTOR_WITH_PARAMETERS + " parameters"));

        final List<Parameter> parameters = Arrays.asList(constructor.getParameters());


        final long countLong = parameters.stream()
                .filter(p -> p.getType().getTypeName().equals(LONG_TYPE))
                .count();


        parameters.stream()
                .filter(p -> p.getType().getTypeName().equals(APPLIANCE_TYPE))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No parameter with type " + APPLIANCE_TYPE));

        parameters.stream()
                .filter(p -> p.getType().getTypeName().equals(BIG_DECIMAL_TYPE))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No parameter with type " + BIG_DECIMAL_TYPE));

        assertEquals(countLong, 2);
    }

    /*Tests for fields*/
    @Test
    void checkCountFields() {
        assertEquals(CLASS_COUNT_FIELDS, allFields.size());
    }

    @Test
    void checkAllFieldsArePrivate() {
        final long count = allFields.stream()
                .filter(p -> Modifier.isPrivate(p.getModifiers()))
                .count();

        assertEquals(CLASS_COUNT_FIELDS, count);
    }
    @ParameterizedTest
    @CsvSource({"id",
            "appliance",
            "amount",
            "number"
    })
    void checkFieldsNames(String name){
        final long count = allFields.stream()
                .filter(f -> f.getName().equals(name))
                .count();
        assertEquals(count,1);
    }
    /*not for student*/
    @ParameterizedTest
    @CsvFileSource(resources = "/OrderRowField.csv")
    void checkNameFieldType(String fieldType, String fieldName) {
        final long countLong = allFields.stream()
                .filter(f -> f.getType().getTypeName().equals(fieldType)
                        & f.getName().equals(fieldName))
                .count();
        assertEquals(1, countLong);
    }
}