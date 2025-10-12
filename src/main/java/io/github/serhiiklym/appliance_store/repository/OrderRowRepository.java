package io.github.serhiiklym.appliance_store.repository;

import io.github.serhiiklym.appliance_store.model.OrderRow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRowRepository extends JpaRepository<OrderRow, Long> {

    List<OrderRow> findAllByOrder_Id(Long orderId);

    boolean existsByOrder_IdAndAppliance_Id(Long orderId, Long applianceId);

    Optional<OrderRow> findByOrder_IdAndAppliance_Id(Long orderId, Long applianceId);

    long deleteByOrder_IdAndId(Long orderId, Long rowId);

}
