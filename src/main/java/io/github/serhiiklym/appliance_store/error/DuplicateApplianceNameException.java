package io.github.serhiiklym.appliance_store.error;

public class DuplicateApplianceNameException extends RuntimeException {
    public DuplicateApplianceNameException(String message) {
        super(message);
    }
}
