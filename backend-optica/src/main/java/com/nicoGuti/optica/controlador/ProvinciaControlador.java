package com.nicoGuti.optica.controlador;

import com.nicoGuti.optica.modelo.Localidad;
import com.nicoGuti.optica.modelo.Provincia;
import com.nicoGuti.optica.servicio.ProvinciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequestMapping("api/provincia")
public class ProvinciaControlador {

    @Autowired
    private ProvinciaService provinciaService;


    @GetMapping
    public ResponseEntity<List<Provincia>> listarProvincias() {
        return ResponseEntity.ok(provinciaService.listarProvincias());
    }

    @GetMapping("/{id}/localidades")
    public ResponseEntity<List<Localidad>> listarLocalidadesPorProvincia(@PathVariable Long id) {
        return ResponseEntity.ok(provinciaService.listarLocalidadesPorProvincia(id));
    }
}
