package com.nicoGuti.optica.repositorio;

import com.nicoGuti.optica.modelo.CuponDescuento;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CuponDescuentoRepositorio extends JpaRepository<CuponDescuento,Long> {
    Optional<CuponDescuento> findByCodigo(String codigo);
    @EntityGraph(attributePaths = {"opticasReferidas"})
    Optional<CuponDescuento> findByOpticaEmisora_Id(Long id);

}
