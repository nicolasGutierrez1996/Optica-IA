package com.nicoGuti.optica.servicio;

import com.nicoGuti.optica.modelo.Direccion;
import com.nicoGuti.optica.repositorio.DireccionRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DireccionServicio {

    @Autowired
    private DireccionRepositorio direccionRepositorio;

    public Direccion guardar(Direccion direccion) {
        return direccionRepositorio.save(direccion);
    }

    public Optional<Direccion> buscarPorId(Long id) {
        return direccionRepositorio.findById(id);
    }

    public List<Direccion> listarTodas() {
        return direccionRepositorio.findAll();
    }

    public void eliminar(Long id) {
        direccionRepositorio.deleteById(id);
    }
}
