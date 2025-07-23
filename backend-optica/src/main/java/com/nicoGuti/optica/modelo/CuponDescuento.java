package com.nicoGuti.optica.modelo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuponDescuento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codigo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "optica_emisora_id")
    @JsonIgnore
    private Optica opticaEmisora;

    @OneToMany(mappedBy = "cuponUtilizado", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Optica> opticasReferidas;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    private boolean beneficioOtorgado = false;

    @PrePersist
    public void generarFecha() {
        this.fechaCreacion = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Cupon{" +
                "id=" + id +
                ", codigo='" + codigo + '\'' +
                // NO accedas a listas o entidades lazy
                '}';
    }
}