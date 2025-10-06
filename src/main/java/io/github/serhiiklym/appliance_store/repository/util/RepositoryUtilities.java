package io.github.serhiiklym.appliance_store.repository.util;

import java.util.Objects;

public class RepositoryUtilities {

    public static String trimInputString(String input) {
        return Objects.requireNonNull(input, "input must not be null").trim();
    }

}
