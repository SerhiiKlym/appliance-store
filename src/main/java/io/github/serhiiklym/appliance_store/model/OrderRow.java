package io.github.serhiiklym.appliance_store.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "ORDERROW")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderRow {

    @Id
    @Column(name = "ORDERROW_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "APPLIANCE_ID")
    @NotNull
    private Appliance appliance;

    @Column(name = "NUMBER", nullable = false)
    @NotNull
    @Positive
    private Long number;

    @Column(name = "AMOUNT", nullable = false)
    @NotNull
    @Positive
    private BigDecimal amount;

}
