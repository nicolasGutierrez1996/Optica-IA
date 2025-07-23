package com.nicoGuti.optica.modelo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class Optica {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 100)
    @Column(nullable = false, unique = true)
    private String nombre;

    @Size(max = 255)
    private String logoUrl;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "direccion_id", nullable = false)
    @NotNull
    private Direccion direccion;

    @NotBlank
    @Size(min = 6, max = 50)
    @Column(nullable = false)
    private String telefono;

    @NotBlank
    @Email
    @Size(min = 5, max = 150)
    @Column(nullable = false, unique = true)
    private String email;

    @Size( max = 500)
    private String descripcion;

    @Column(nullable = false)
    private boolean activo = true;

    @Column(name = "fecha_creacion")
    private LocalDateTime fecha_creacion;

    @Column(name = "fecha_ultima_actualizacion")
    private LocalDateTime fecha_ultima_actualizacion;

    @ManyToOne
    @JoinColumn(name = "cupon_usado_id")
    @JsonIgnore
    private CuponDescuento cuponUtilizado;

    @PrePersist
    private void antesDePersistir(){
        this.fecha_creacion = LocalDateTime.now();
    }

    @PreUpdate
    private void antesDeModificar(){
        this.fecha_ultima_actualizacion = LocalDateTime.now();
    }
}