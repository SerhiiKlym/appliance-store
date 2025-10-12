package io.github.serhiiklym.appliance_store.error;

public class OrderIsApprovedAndClosedToModificationsException extends RuntimeException {
    public OrderIsApprovedAndClosedToModificationsException(Long orderId) {
        super("The order is already approved — no further modifications allowed (orderId=" + orderId + ")");
    }
}
