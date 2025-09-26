package io.github.serhiiklym.appliance_store.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

import static jakarta.persistence.EnumType.STRING;

@Entity
@Table(name = "APPLIANCE")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Appliance {

    @Id
    @Column(name = "APPLIANCE_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MANUFACTURER_ID", nullable = false) // FK
    @NotNull
    private Manufacturer manufacturer;

    @Column(name = "NAME", nullable = false)
    @NotBlank
    private String name;

    @Column(name = "CATEGORY")
    @Enumerated(STRING)
    private Category category;

    @Column(name = "MODEL")
    @NotBlank
    private String model;

    @Column(name = "POWER_TYPE")
    @Enumerated(STRING)
    private PowerType powerType;

    @Column(name = "CHARACTERISTIC")
    private String characteristic;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "POWER")
    @NotNull
    @Positive
    private Integer power;

    @Column(name = "PRICE")
    @NotNull
    @Positive
    private BigDecimal price;

}
