package io.github.serhiiklym.appliance_store.service.impl;

import io.github.serhiiklym.appliance_store.error.OrderIsApprovedAndClosedToModificationsException;
import io.github.serhiiklym.appliance_store.model.*;
import io.github.serhiiklym.appliance_store.repository.*;
import io.github.serhiiklym.appliance_store.service.OrderService;
import io.github.serhiiklym.appliance_store.service.impl.util.TestClients;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;

import static io.github.serhiiklym.appliance_store.service.impl.util.TestAppliances.persistApplianceAllFields;
import static io.github.serhiiklym.appliance_store.service.impl.util.TestClients.makeValidClient;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @DataJpaTest boots JPA, repositories, and an embedded DB.
 * We @Import the service to exercise real SQL + mappings.
 */
@DataJpaTest
@Import(OrderServiceImpl.class)
class OrderServiceJpaSliceTest {

    @Autowired private OrderService orderService; // the real service under test
    @Autowired private OrdersRepository ordersRepository;
    @Autowired private OrderRowRepository orderRowRepository;
    @Autowired private ClientRepository clientRepository;
    @Autowired private ApplianceRepository applianceRepository;
    @Autowired private ManufacturerRepository manufacturerRepository;
    @Autowired private EntityManager em;

    private Client persistClient(String name) {
        Client c = new Client();
        c.setName(name);
        return clientRepository.save(c);
    }

    private Appliance persistAppliance(String name, String price) {
        Appliance a = new Appliance();
        a.setName(name);
        a.setPrice(new BigDecimal(price));
        return applianceRepository.save(a);
    }

    private void flushAndClear() {
        em.flush();
        em.clear(); // ensure we read back from DB, not from the persistence context
    }

    @Test
    @DisplayName("createOrder persists and returns a managed entity with generated ID")
    void createOrder_persists_and_generates_id() {
        Client client = clientRepository.saveAndFlush(makeValidClient("Alice"));

        Orders saved = orderService.createOrder(client.getId());

        assertThat(saved.getId()).as("ID should be generated").isNotNull();
        assertThat(saved.getApproved()).isFalse();
        assertThat(saved.getClient().getId()).isEqualTo(client.getId());
        assertThat(saved.getOrderRowSet()).isNotNull().isEmpty();

        flushAndClear();

        Orders reloaded = ordersRepository.findById(saved.getId()).orElseThrow();
        assertThat(reloaded.getApproved()).isFalse();
        assertThat(reloaded.getClient().getId()).isEqualTo(client.getId());
        assertThat(reloaded.getOrderRowSet()).isEmpty();
    }

    @Test
    @DisplayName("addRow persists child rows via cascade and computes amount with HALF_UP to 2 decimals")
    void addRow_persists_row_via_cascade_and_rounds() {
        // Arrange: persist valid client and appliance
        Client client = clientRepository.saveAndFlush(TestClients.makeValidClient("Bob"));
        Appliance appl = persistApplianceAllFields(
                applianceRepository, manufacturerRepository,
                "BrandX", "Kettle", "KT-1", "19.995",
                Category.SMALL, PowerType.AC220
        );

        // Persist order (gets an ID)
        Orders order = orderService.createOrder(client.getId());
        // If createOrder() uses save(...), ensure the row is in DB before addRow():
        ordersRepository.flush();

        // Sanity guards
        assertThat(order.getId()).isNotNull();
        assertThat(appl.getId()).isNotNull();

        // Act
        orderService.addRow(order.getId(), appl.getId(), 1L);

        flushAndClear();

        // Assert
        Orders reloaded = ordersRepository.findById(order.getId()).orElseThrow();
        assertThat(reloaded.getOrderRowSet()).hasSize(1);
        OrderRow row = reloaded.getOrderRowSet().iterator().next();
        assertThat(row.getAppliance().getId()).isEqualTo(appl.getId());
        assertThat(row.getNumber()).isEqualTo(1L);
        assertThat(row.getAmount()).isEqualByComparingTo(new java.math.BigDecimal("20.00"));
    }


    @Test
    @DisplayName("removing a row and saving the order deletes the child via orphanRemoval (if configured)")
    void orphanRemoval_removes_child() {
        Client client = clientRepository.saveAndFlush(makeValidClient("Carol"));
        Appliance appl = persistApplianceAllFields(
                applianceRepository, manufacturerRepository,
                "BrandX", "Toaster", "KT-1", "25.00",
                Category.SMALL, PowerType.AC110);
        //Appliance appl = persistAppliance("Toaster", "25.00");
        Orders order = orderService.createOrder(client.getId());
        orderService.addRow(order.getId(), appl.getId(), 2L);

        flushAndClear();

        // load fresh aggregate from DB
        Orders reloaded = ordersRepository.findById(order.getId()).orElseThrow();
        assertThat(reloaded.getOrderRowSet()).hasSize(1);
        OrderRow row = reloaded.getOrderRowSet().iterator().next();
        Long rowId = row.getId(); // requires @Id on OrderRow

        // remove child from collection and save the parent
        reloaded.getOrderRowSet().remove(row);
        ordersRepository.save(reloaded);

        flushAndClear();

        // expect the child row to be gone if orphanRemoval=true
        boolean exists = orderRowRepository.findById(rowId).isPresent();
        assertThat(exists)
                .as("OrderRow should be deleted when removed from parent collection (orphanRemoval=true)")
                .isFalse();
    }

    @Test
    @DisplayName("cannot add rows to an approved order (domain guard)")
    void addRow_rejected_when_order_approved() {
        Client client = clientRepository.saveAndFlush(makeValidClient("Dave"));
        Appliance appl = persistApplianceAllFields(
                applianceRepository, manufacturerRepository,
                "BrandX", "Microwave", "KT-1", "100.00",
                Category.BIG, PowerType.AC110);
        //Appliance appl = persistAppliance("Microwave", "100.00");
        Orders order = orderService.createOrder(client.getId());

        // flip approved and persist (simulates approval; replace with approveOrder() when implemented)
        order.setApproved(true);
        ordersRepository.save(order);
        flushAndClear();

        assertThrows(OrderIsApprovedAndClosedToModificationsException.class,
                () -> orderService.addRow(order.getId(), appl.getId(), 1L));
    }
}
