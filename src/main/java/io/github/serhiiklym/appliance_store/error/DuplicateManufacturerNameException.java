package io.github.serhiiklym.appliance_store.error;

public class DuplicateManufacturerNameException extends RuntimeException {
    public DuplicateManufacturerNameException(String message) {
        super(message);
    }
}
