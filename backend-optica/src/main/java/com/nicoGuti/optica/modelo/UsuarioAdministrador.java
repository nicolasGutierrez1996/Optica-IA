package com.nicoGuti.optica.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioAdministrador {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 4, max = 50)
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank
    @Size(min = 2, max = 100)
    @Column(nullable = false)
    private String nombre;

    @NotBlank
    @Size(min = 2, max = 100)
    @Column(nullable = false)
    private String apellido;

    @Column(length = 20, unique = true)
    private String dni;

    @NotBlank
    @Size(min = 6, max = 100)
    @Column(nullable = false)
    private String password;

    @NotBlank
    @Email
    @Size(max = 100)
    @Column(nullable = false, unique = true)
    private String email;

    @ManyToOne(optional = true)
    @JoinColumn(name = "optica_id", nullable = true)
    private Optica optica;

    @Column(name = "fecha_creacion")
    private LocalDateTime fecha_creacion;

    @Column(name = "fecha_ultima_actualizacion")
    private LocalDateTime fecha_ultima_actualizacion;

    @Column(name = "token")
    private String token;

    @Column(name = "tokenExpiracion")
    private LocalDateTime tokenExpiracion;

    @Column(nullable = false)
    private boolean verificado = false;

    @PrePersist
    private void antesDePersistir(){
        this.fecha_creacion=LocalDateTime.now();
    }
    @PreUpdate
    private void antesDeModificar(){
        this.fecha_ultima_actualizacion=LocalDateTime.now();
    }
}