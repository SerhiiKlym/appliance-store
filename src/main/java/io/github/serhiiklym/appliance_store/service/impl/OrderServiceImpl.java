package io.github.serhiiklym.appliance_store.service.impl;

import io.github.serhiiklym.appliance_store.error.DuplicateOrderIdException;
import io.github.serhiiklym.appliance_store.error.NotFoundException;
import io.github.serhiiklym.appliance_store.error.OrderIsApprovedAndClosedToModificationsException;
import io.github.serhiiklym.appliance_store.model.Appliance;
import io.github.serhiiklym.appliance_store.model.Client;
import io.github.serhiiklym.appliance_store.model.OrderRow;
import io.github.serhiiklym.appliance_store.model.Orders;
import io.github.serhiiklym.appliance_store.repository.ApplianceRepository;
import io.github.serhiiklym.appliance_store.repository.ClientRepository;
import io.github.serhiiklym.appliance_store.repository.OrderRowRepository;
import io.github.serhiiklym.appliance_store.repository.OrdersRepository;
import io.github.serhiiklym.appliance_store.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Locale;

@Slf4j
@Service
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final ClientRepository clientRepository;
    private final OrdersRepository ordersRepository;
    private final ApplianceRepository applianceRepository;
    private final OrderRowRepository orderRowRepository;

    public OrderServiceImpl(ClientRepository clientRepository, OrdersRepository ordersRepository, ApplianceRepository applianceRepository, OrderRowRepository orderRowRepository) {
        this.clientRepository = clientRepository;
        this.ordersRepository = ordersRepository;
        this.applianceRepository = applianceRepository;
        this.orderRowRepository = orderRowRepository;
    }

    @Override
    public Page<Orders> list(Pageable pageable) {
        return null;
    }

    @Override
    public Page<Orders> listByClient(Long clientId, Pageable pageable) {
        return null;
    }

    @Override
    public Page<Orders> listByApproved(boolean approved, Pageable pageable) {
        return null;
    }

    @Override
    public BigDecimal getTotal(Long orderId) {
        return null;
    }

    @Override
    @Transactional
    public Orders createOrder(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new NotFoundException("Client not found with ID: " + clientId));
        log.debug("Creating an Order for client ID={}", clientId);
        Orders order = new Orders();
        order.setClient(client);
        order.setApproved(false);
        if (order.getOrderRowSet() == null) {
            order.setOrderRowSet(new HashSet<>());
        }

        try {
            Orders saved = ordersRepository.save(order);
            log.info("Created order id={}, client card={}", saved.getId(), saved.getClient().getCard());
            return saved;
        } catch (DataIntegrityViolationException e) {
            log.warn("Duplicate order on create with client ID={}", clientId, e);
            throw new DuplicateOrderIdException(
                    String.format(Locale.ROOT, "Order id is already exists for client '%d'", clientId)
            );
        }
    }

    @Override
    @Transactional
    public Orders addRow(Long orderId, Long applianceId, Long qty) {
        if (qty == null || qty <= 0) {
            throw new IllegalArgumentException("Quantity must be a positive integer");
        }

        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId));

        if (Boolean.TRUE.equals(order.getApproved())) throw new OrderIsApprovedAndClosedToModificationsException(orderId);

        Appliance appliance = applianceRepository.findById(applianceId)
                .orElseThrow(() -> new NotFoundException("Appliance not found with ID: " + applianceId));

        if (order.getOrderRowSet() == null) {
            order.setOrderRowSet(new HashSet<>());
        }

        // Merge: if the appliance is already in the order -> increase quantity
        OrderRow row = order.getOrderRowSet().stream()
                .filter(r -> r.getAppliance() != null
                        && r.getAppliance().getId().equals(applianceId))
                .findFirst()
                .orElse(null);

        if (row == null) {
            row = new OrderRow();
            row.setOrder(order);                                    // owning side (FK lives on child)
            row.setAppliance(appliance);
            row.setNumber(qty);
            row.setAmount(calcAmount(appliance.getPrice(), qty));
            order.getOrderRowSet().add(row);                        // inverse side
        } else {
            long newQty = row.getNumber() + qty;
            row.setNumber(newQty);
            row.setAmount(calcAmount(appliance.getPrice(), newQty));
        }

        return ordersRepository.save(order);
    }

    @Override
    public Orders updateRowQuantity(Long orderId, Long rowId, int qty) {
        return null;
    }

    @Override
    public void removeRow(Long orderId, Long rowId) {

    }

    @Override
    public Orders approveOrder(Long orderId, Long employeeId) {
        return null;
    }

    // -- helpers --
    private static BigDecimal calcAmount(BigDecimal unitPrice, long qty) {
        // Currency math with explicit scale and rounding
        return unitPrice
                .multiply(BigDecimal.valueOf(qty))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
