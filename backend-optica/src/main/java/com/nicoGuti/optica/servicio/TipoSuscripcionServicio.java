package com.nicoGuti.optica.servicio;

import com.nicoGuti.optica.modelo.TipoSuscripcion;
import com.nicoGuti.optica.repositorio.TipoSuscripcionRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TipoSuscripcionServicio {
    private final TipoSuscripcionRepositorio tipoSuscripcionRepository;

    public List<TipoSuscripcion> listarTodas() {
        return tipoSuscripcionRepository.findAll();
    }

    public Optional<TipoSuscripcion> buscarPorId(Long id) {
        return tipoSuscripcionRepository.findById(id);
    }
}
