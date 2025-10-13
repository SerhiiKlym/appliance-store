package io.github.serhiiklym.appliance_store.service.impl;

import io.github.serhiiklym.appliance_store.error.NotFoundException;

import io.github.serhiiklym.appliance_store.error.OrderIsApprovedAndClosedToModificationsException;
import io.github.serhiiklym.appliance_store.model.Appliance;
import io.github.serhiiklym.appliance_store.model.OrderRow;
import io.github.serhiiklym.appliance_store.model.Orders;
import io.github.serhiiklym.appliance_store.repository.ApplianceRepository;
import io.github.serhiiklym.appliance_store.repository.ClientRepository;
import io.github.serhiiklym.appliance_store.repository.OrdersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

import java.math.BigDecimal;
import java.util.Optional;


import static io.github.serhiiklym.appliance_store.service.impl.util.TestAppliances.appliance;
import static io.github.serhiiklym.appliance_store.service.impl.util.TestClients.client;
import static io.github.serhiiklym.appliance_store.service.impl.util.TestOrders.draftOrder;
import static io.github.serhiiklym.appliance_store.service.impl.util.TestOrders.rowFor;
import static java.math.RoundingMode.HALF_UP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class OrderServiceImplTest {

    @Mock private ClientRepository clientRepository;
    @Mock private OrdersRepository ordersRepository;
    @Mock private ApplianceRepository applianceRepository;

    @InjectMocks
    private OrderServiceImpl service;

    @BeforeEach
    void stubSaveVariants() {
        // Allow either save(..) or saveAndFlush(..) internally
        when(ordersRepository.save(any(Orders.class))).thenAnswer(inv -> inv.getArgument(0));
        when(ordersRepository.saveAndFlush(any(Orders.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Nested
    class CreateOrderTests {

        @Test
        @DisplayName("creates draft order for an existing client")
        void createsDraftOrder() {

            Long clientId = 101L;
            when(clientRepository.findById(clientId))
                    .thenReturn(Optional.of(client(clientId)));

            when(ordersRepository.save(any(Orders.class)))
                    .thenAnswer(inv -> {
                        Orders o = inv.getArgument(0);
                        o.setId(555L);           // simulate generated PK
                        return o;
                    });


            Orders saved = service.createOrder(clientId);

            assertThat(saved.getId()).isNotNull();          // set by stub
            assertThat(saved.getApproved()).isFalse();
            assertThat(saved.getClient().getId()).isEqualTo(clientId);
            assertThat(saved.getOrderRowSet()).isNotNull();
            assertThat(saved.getOrderRowSet()).isEmpty();

            verify(ordersRepository, atLeastOnce()).save(any());
            verify(ordersRepository, never()).saveAndFlush(any());
        }

        @Test
        @DisplayName("throws NotFoundException when client is missing")
        void throwsWhenClientMissing() {
            Long clientId = 909L;
            when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> service.createOrder(clientId));

            verify(ordersRepository, never()).save(any());
            verify(ordersRepository, never()).saveAndFlush(any());
        }
    }

    @Nested
    class AddRowTests {

        @Test
        @DisplayName("adds a new row when appliance not present")
        void addsNewRow() {
            Long orderId = 201L, applId = 301L;
            Orders order = draftOrder(orderId, client(77L));
            Appliance appl = appliance(applId, new BigDecimal("19.99"));

            when(ordersRepository.findById(orderId)).thenReturn(Optional.of(order));
            when(applianceRepository.findById(applId)).thenReturn(Optional.of(appl));

            Orders updated = service.addRow(orderId, applId, 3L);

            assertThat(updated.getOrderRowSet()).hasSize(1);
            OrderRow r = updated.getOrderRowSet().iterator().next();
            assertThat(r.getAppliance().getId()).isEqualTo(applId);
            assertThat(r.getNumber()).isEqualTo(3L); // assuming NUMBER == quantity
            assertThat(r.getAmount()).isEqualByComparingTo(new BigDecimal("59.97").setScale(2, HALF_UP));

            verify(ordersRepository, atLeastOnce()).save(any(Orders.class));
        }

        @Test
        @DisplayName("merges quantity when same appliance already present")
        void mergesQuantity() {
            Long orderId = 202L, applId = 302L;
            Orders order = draftOrder(orderId, client(88L));
            Appliance appl = appliance(applId, new BigDecimal("10.00"));

            // existing row qty=2, amount=20.00
            order.getOrderRowSet().add(rowFor(appl, 2L, new BigDecimal("20.00")));

            when(ordersRepository.findById(orderId)).thenReturn(Optional.of(order));
            when(applianceRepository.findById(applId)).thenReturn(Optional.of(appl));

            Orders updated = service.addRow(orderId, applId, 3L);

            assertThat(updated.getOrderRowSet()).hasSize(1); // merged, not duplicated
            OrderRow r = updated.getOrderRowSet().iterator().next();
            assertThat(r.getNumber()).isEqualTo(5L);
            assertThat(r.getAmount()).isEqualByComparingTo(new BigDecimal("50.00").setScale(2, HALF_UP));
        }

        @Test
        @DisplayName("rejects non-positive quantity")
        void rejectsNonPositiveQty() {
            Long orderId = 203L, applId = 303L;

            assertThrows(IllegalArgumentException.class, () -> service.addRow(orderId, applId, 0L));
            assertThrows(IllegalArgumentException.class, () -> service.addRow(orderId, applId, -1L));

            verifyNoInteractions(applianceRepository);
            // ordersRepository may be touched before qty validation depending on your implementation;
            // if you validate first, then also verifyNoInteractions(ordersRepository)
        }

        @Test
        @DisplayName("throws when order not found")
        void throwsWhenOrderMissing() {
            when(ordersRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> service.addRow(1L, 2L, 1L));
            verify(applianceRepository, never()).findById(anyLong());
        }

        @Test
        @DisplayName("throws when appliance not found")
        void throwsWhenApplianceMissing() {
            Long orderId = 204L;
            Orders order = draftOrder(orderId, client(99L));
            when(ordersRepository.findById(orderId)).thenReturn(Optional.of(order));
            when(applianceRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> service.addRow(orderId, 999L, 1L));
        }

        @Test
        @DisplayName("rejects add when order is approved")
        void rejectsWhenApproved() {
            Long orderId = 205L, applId = 305L;
            Orders order = draftOrder(orderId, client(11L));
            order.setApproved(true);

            when(ordersRepository.findById(orderId)).thenReturn(Optional.of(order));

            assertThrows(OrderIsApprovedAndClosedToModificationsException.class,
                    () -> service.addRow(orderId, applId, 1L));
            verify(applianceRepository, never()).findById(anyLong());
            verify(ordersRepository, never()).save(any());
        }

        @Test
        @DisplayName("rounds HALF_UP to scale 2")
        void roundsHalfUp() {
            Long orderId = 206L, applId = 306L;
            Orders order = draftOrder(orderId, client(12L));
            Appliance appl = appliance(applId, new BigDecimal("19.995")); // boundary

            when(ordersRepository.findById(orderId)).thenReturn(Optional.of(order));
            when(applianceRepository.findById(applId)).thenReturn(Optional.of(appl));

            Orders updated = service.addRow(orderId, applId, 1L);

            OrderRow r = updated.getOrderRowSet().iterator().next();
            assertThat(r.getAmount()).isEqualByComparingTo(new BigDecimal("20.00"));
        }
    }
}