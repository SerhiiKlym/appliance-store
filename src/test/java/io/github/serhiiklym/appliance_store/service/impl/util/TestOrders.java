package io.github.serhiiklym.appliance_store.service.impl.util;

import io.github.serhiiklym.appliance_store.model.Appliance;
import io.github.serhiiklym.appliance_store.model.Client;
import io.github.serhiiklym.appliance_store.model.OrderRow;
import io.github.serhiiklym.appliance_store.model.Orders;
import io.github.serhiiklym.appliance_store.repository.OrderRowRepository;
import io.github.serhiiklym.appliance_store.repository.OrdersRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    /** Build a valid draft order (not persisted). */
    public static Orders makeDraftOrder(Client client) {
        Orders o = new Orders();
        o.setClient(client);
        o.setApproved(false);
        o.setOrderRowSet(new HashSet<>());
        // If your model has an employee approver field, leave it null for drafts.
        return o;
    }

    /** Persist a draft order using the repo provided by the test. */
    public static Orders persistDraftOrder(OrdersRepository repo, Client client) {
        return repo.saveAndFlush(makeDraftOrder(client));
    }

    /**
     * Create a new OrderRow for a given appliance & qty, attach it to the order,
     * and keep both sides of the association in sync.
     *  */
    public static OrderRow attachRow(Orders order, Appliance appliance, long qty) {
        OrderRow row = new OrderRow();
        row.setOrder(order);                 // owning side set
        row.setAppliance(appliance);
        row.setNumber(qty);
        row.setAmount(appliance.getPrice()
                .multiply(BigDecimal.valueOf(qty))
                .setScale(2, RoundingMode.HALF_UP));
        order.getOrderRowSet().add(row);     // inverse side set
        return row;
    }

    /** Persist the parent order; with cascade PERSIST/MERGE this will save rows too. */
    public static Orders saveGraph(OrdersRepository repo, Orders order) {
        return repo.saveAndFlush(order);
    }

    /** If you don't have cascade, persist the row explicitly. */
    public static OrderRow persistRow(OrderRowRepository rowRepo, OrderRow row) {
        return rowRepo.saveAndFlush(row);
    }
}

