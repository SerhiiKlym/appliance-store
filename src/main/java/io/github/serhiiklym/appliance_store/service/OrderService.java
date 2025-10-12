package io.github.serhiiklym.appliance_store.service;

import io.github.serhiiklym.appliance_store.model.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface OrderService {
    Page<Orders> list(Pageable pageable);
    Page<Orders> listByClient(Long clientId, Pageable pageable);
    Page<Orders> listByApproved(boolean approved, Pageable pageable);
    BigDecimal getTotal(Long orderId);

    Orders createOrder (Long clientId);
    // --  only if not approved
    /**
     * Add a row with the given appliance and quantity.
     * If a row for the same appliance exists in the same order, quantities are merged.
     */
    Orders addRow (Long orderId, Long applianceId, Long qty);

    /**
     * Change quantity of a specific row.
     * If newQty == 0, the row is auto-deleted.
     */
    Orders updateRowQuantity (Long orderId, Long rowId, int qty);
    void removeRow(Long orderId, Long rowId);

    /**
     * Approve a non-empty draft order and record the approver (employee).
     * After approval the order becomes immutable.
     */
    Orders approveOrder(Long orderId, Long employeeId);
}
