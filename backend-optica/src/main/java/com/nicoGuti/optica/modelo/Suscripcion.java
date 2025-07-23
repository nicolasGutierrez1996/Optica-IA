package com.nicoGuti.optica.modelo;

import com.nicoGuti.optica.modelo.enumeradores.EstadoSuscripcion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Suscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private UsuarioAdministrador usuario;

    @ManyToOne(optional = false)
    private TipoSuscripcion tipoSuscripcion;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    private LocalDate fechaVencimiento;

    private BigDecimal montoFinal;

    private boolean activa = true;

    @Enumerated(EnumType.STRING)
    private EstadoSuscripcion estado;

    private BigDecimal descuentoAplicado = BigDecimal.ZERO;
}