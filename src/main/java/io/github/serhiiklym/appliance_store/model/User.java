package io.github.serhiiklym.appliance_store.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
public class User {

    @Id
    @Column(name = "USER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NAME")
    @NotBlank
    private String name;

    @Column(name = "EMAIL")
    @Email
    @NotBlank
    private String email;

    @Column(name = "PASSWORD")
    @NotBlank
    private String password;

}
