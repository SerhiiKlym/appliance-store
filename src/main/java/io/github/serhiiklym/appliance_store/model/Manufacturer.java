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


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Manufacturer that = (Manufacturer) o;
        return getId().equals(that.getId()) && getName().equals(that.getName());
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getName().hashCode();
        return result;
    }
}
