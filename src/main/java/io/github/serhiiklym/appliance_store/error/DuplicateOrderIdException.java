package io.github.serhiiklym.appliance_store.error;

public class DuplicateOrderIdException extends RuntimeException {
    public DuplicateOrderIdException(String message) {
        super(message);
    }
}
