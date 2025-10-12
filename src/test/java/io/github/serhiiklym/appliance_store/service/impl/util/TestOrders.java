package io.github.serhiiklym.appliance_store.service.impl.util;

import io.github.serhiiklym.appliance_store.model.Appliance;
import io.github.serhiiklym.appliance_store.model.Client;
import io.github.serhiiklym.appliance_store.model.OrderRow;
import io.github.serhiiklym.appliance_store.model.Orders;

import java.math.BigDecimal;
import java.util.HashSet;

public final class TestOrders {
    private TestOrders() {}

    public static Orders draftOrder(Long id, Client client) {
        Orders o = new Orders();
        o.setId(id);
        o.setClient(client);
        o.setApproved(false);
        o.setOrderRowSet(new HashSet<>());
        return o;
    }

    public static OrderRow rowFor(Appliance appliance, long qty, BigDecimal amount) {
        OrderRow r = new OrderRow();
        r.setAppliance(appliance);
        r.setNumber(qty); // assuming NUMBER is the quantity column
        r.setAmount(amount);
        return r;
    }
}

