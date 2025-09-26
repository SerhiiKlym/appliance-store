package io.github.serhiiklym.appliance_store.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "MANUFACTURER")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Manufacturer {

    @Id
    @Column(name = "MANUFACTURER_ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Column(name = "NAME", nullable = false, length = 30, unique = true)
    @NotBlank
    @Setter
    @Getter
    private String name;

}
