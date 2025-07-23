package com.nicoGuti.optica.modelo.dto;

public record CuponDTO(
        String codigo,
        boolean beneficioOtorgado,
        int cantidadReferidas
) {}