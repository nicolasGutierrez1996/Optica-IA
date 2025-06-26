package com.nicoGuti.optica.servicio;

import com.nicoGuti.optica.modelo.TipoAnteojo;
import com.nicoGuti.optica.repositorio.TipoAnteojoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TipoAnteojoServicio {
    @Autowired
    private TipoAnteojoRepositorio tipoAnteojoRepository;

    public TipoAnteojo guardar(TipoAnteojo tipo) {
        return tipoAnteojoRepository.save(tipo);
    }

    public Optional<TipoAnteojo> buscarPorId(Long id) {
        return tipoAnteojoRepository.findById(id);
    }

    public List<TipoAnteojo> listarTodos() {
        return tipoAnteojoRepository.findAll();
    }

    public void eliminar(Long id) {
        tipoAnteojoRepository.deleteById(id);
    }
}
