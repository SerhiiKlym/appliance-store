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
@Table(name = "EMPLOYEE")
@NoArgsConstructor
@Getter
@Setter
public class Employee extends User {

    @Column(name = "DEPARTMENT")
    @NotBlank
    private String department;

    @Autowired
    public Employee(Long id, String name, String email, String password, String department) {
        super(id, name, email, password);
        this.department = department;
    }

}
