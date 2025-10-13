package io.github.serhiiklym.appliance_store.repository;

import io.github.serhiiklym.appliance_store.model.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrdersRepository extends JpaRepository<Orders, Long> {

    Page<Orders> findAllByApproved(boolean approved, Pageable pageable);

    Page<Orders>  findAllByClient_Id(Long clientId, Pageable pageable);

    Optional<Orders> findByIdAndClient_Id(Long orderId, Long clientId);

    boolean existsByIdAndClient_Id(Long orderId, Long clientId);

}
