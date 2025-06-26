package com.nicoGuti.optica.servicio;

import com.nicoGuti.optica.modelo.Optica;
import com.nicoGuti.optica.repositorio.OpticaRepositorio;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OpticaServicio {

    private final OpticaRepositorio opticaRepo;

    public OpticaServicio(OpticaRepositorio opticaRepo) {
        this.opticaRepo = opticaRepo;
    }

    public List<Optica> obtenerTodas() {
        return opticaRepo.findAll();
    }

    public Optional<Optica> buscarPorId(Long id) {
        return opticaRepo.findById(id);
    }

    public Optica guardar(Optica optica) {
        return opticaRepo.save(optica);
    }

    public void eliminar(Long id) {
        opticaRepo.deleteById(id);
    }

    public Optional<Optica> buscarPorEmail(String email) {
        return opticaRepo.findByEmail(email);
    }
}
