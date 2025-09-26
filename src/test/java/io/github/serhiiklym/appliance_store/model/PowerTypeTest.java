package io.github.serhiiklym.appliance_store.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static io.github.serhiiklym.appliance_store.model.TestConstants.POWER_TYPE_TYPE;
import static io.github.serhiiklym.appliance_store.model.TestConstants.PowerType.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PowerTypeTest {
    private static List<?> constants;
    private static Class<?> clazz;

    @BeforeAll
    static void setup() throws ClassNotFoundException {
        clazz = Class.forName(POWER_TYPE_TYPE);
        constants = Arrays.asList(clazz.getEnumConstants());
    }

    @Test
    @DisplayName("Class " + POWER_TYPE_TYPE + " is Enum")
    void checkIsEnum() {
        final boolean actual = clazz.isEnum();
        assertTrue(actual);
    }

    @Test
    @DisplayName("There must be " + ENUM_COUNT_CONSTANTS + " constants")
    void checkCountConstants() {
        int actual = constants.size();
        assertEquals(ENUM_COUNT_CONSTANTS, actual);
    }

    @Test
    @DisplayName("There must be constant with name " + ENUM_CONSTANT_AC220)
    void checkEnumConstantAC220() {
        long actual = Arrays.stream(clazz.getDeclaredFields())
                .map(f -> f.getName())
                .filter(name -> name.equals(ENUM_CONSTANT_AC220))
                .count();
        assertEquals(1, actual);
    }

    @Test
    @DisplayName("There must be constant with name " + ENUM_CONSTANT_AC110)
    void checkEnumConstantAC110() {
        long actual = Arrays.stream(clazz.getDeclaredFields())
                .map(f -> f.getName())
                .filter(name -> name.equals(ENUM_CONSTANT_AC110))
                .count();
        assertEquals(1, actual);
    }

    @Test
    @DisplayName("There must be constant with name " + ENUM_CONSTANT_ACCUMULATOR)
    void checkEnumConstantACCUMULATOR() {
        long actual = Arrays.stream(clazz.getDeclaredFields())
                .map(f -> f.getName())
                .filter(name -> name.equals(ENUM_CONSTANT_ACCUMULATOR))
                .count();
        assertEquals(1, actual);
    }
}