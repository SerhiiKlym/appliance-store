package io.github.serhiiklym.appliance_store.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

@Entity
@Table(name = "CLIENT")
@NoArgsConstructor
@Getter
@Setter
public class Client extends User {

    @Autowired
    public Client(Long id, String name, String email, String password, String card) {
        super(id, name, email, password);
        this.card = card;
    }

    @Column(name = "CARD")
    @NotBlank
    private String card;

}
