package io.github.serhiiklym.appliance_store.model;

import org.junit.jupiter.api.BeforeEach;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrdersTest {

    private static List<Field> allFields;
    private static List<Constructor<?>> allConstructors;

    @BeforeEach
    void setUp() throws ClassNotFoundException {
        final Class<?> userClass = Class.forName(TestConstants.ORDERS_TYPE);
        allFields = Arrays.asList(userClass.getDeclaredFields());
        allConstructors = Arrays.asList(userClass.getConstructors());
    }

    /*Tests for constructors*/
    @Test
    void checkCountConstructors() {
        assertEquals(TestConstants.Orders.CLASS_COUNT_CONSTRUCTORS, allConstructors.size());
    }

    @Test
    void checkModifiersConstructors() {
        final boolean actual = allConstructors.stream()
                .allMatch(c -> Modifier.isPublic(c.getModifiers()));
        assertTrue(actual);
    }

    @Test
    void checkDefaultConstructor() {
        final long count = allConstructors.stream()
                .filter(c -> c.getParameterCount() == 0)
                .count();
        assertEquals(1, count);
    }

    @Test
    void checkConstructorWithParameter() {
        final long count = allConstructors.stream()
                .filter(c -> c.getParameterCount() == TestConstants.Orders.PARAMETERS_IN_CONSTRUCTOR_WITH_PARAMETERS)
                .count();
        assertEquals(1, count);
    }

    @Test
    void checkParameterTypeForConstructorWithParameter() {
        final Constructor<?> constructor = allConstructors.stream()
                .filter(c -> c.getParameterCount() == TestConstants.Orders.PARAMETERS_IN_CONSTRUCTOR_WITH_PARAMETERS)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No constructor with " + TestConstants.Orders.PARAMETERS_IN_CONSTRUCTOR_WITH_PARAMETERS + " parameters"));

        final List<Parameter> parameters = Arrays.asList(constructor.getParameters());

        parameters.stream()
                .filter(p -> p.getType().getTypeName().equals(TestConstants.LONG_TYPE))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No parameter with type " + TestConstants.LONG_TYPE));

        parameters.stream()
                .filter(p -> p.getType().getTypeName().equals(TestConstants.CLIENT_TYPE))
                .findFirst()
                .orElseThrow(()->new RuntimeException("No parameter with type "+TestConstants.CLIENT_TYPE));

        parameters.stream()
                .filter(p -> p.getType().getTypeName().equals(TestConstants.EMPLOYEE_TYPE))
                .findFirst()
                .orElseThrow(()->new RuntimeException("No parameter with type "+TestConstants.EMPLOYEE_TYPE));

        parameters.stream()
                .filter(p -> p.toString().contains("java.util.Set<io.github.serhiiklym.appliance_store.model.OrderRow>"))
                .findFirst()
                .orElseThrow(()->new RuntimeException("No parameter with type java.util.Set<io.github.serhiiklym.appliance_store.model.OrderRow>"));
    }

    /*Tests for fields*/
    @Test
    void checkCountFields(){
        assertEquals(TestConstants.Orders.CLASS_COUNT_FIELDS,allFields.size());
    }

    @Test
    void checkAllFieldsArePrivate(){
        final boolean isPrivate = allFields.stream()
                .allMatch(f -> Modifier.isPrivate(f.getModifiers()));
        assertTrue(isPrivate);
    }
    @ParameterizedTest
    @CsvSource({"id",
            "client",
            "employee",
            "orderRowSet"
    })
    void checkFieldsNames(String name){
        final long count = allFields.stream()
                .filter(f -> f.getName().equals(name))
                .count();
        assertEquals(1,count);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/OrdersField.csv")
    void checkNameFieldType(String fieldType, String fieldName, long expected) {
        final long countLong = allFields.stream()
                .filter(f -> f.getType().getTypeName().equals(fieldType)
                        & f.getName().equals(fieldName))
                .count();
        assertEquals(expected, countLong);
    }
}