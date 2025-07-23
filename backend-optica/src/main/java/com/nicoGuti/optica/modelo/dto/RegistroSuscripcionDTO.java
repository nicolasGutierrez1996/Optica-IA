package com.nicoGuti.optica.modelo.dto;

import lombok.Data;

@Data
public class RegistroSuscripcionDTO {
    private Long idOptica;
    private Long idTipoSuscripcion;
    private String codigoCupon;
    private Long idUsuarioAdministrador;
}
