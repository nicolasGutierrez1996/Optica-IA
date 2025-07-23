package com.nicoGuti.optica.controlador;

import com.nicoGuti.optica.modelo.TipoSuscripcion;
import com.nicoGuti.optica.servicio.TipoSuscripcionServicio;
import com.nicoGuti.optica.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-suscripcion")

public class TipoSuscripcionControlador {

    @Autowired
    private TipoSuscripcionServicio tipoSuscripcionServicio;

    @GetMapping
    public ResponseEntity<?> listar() {
        List<TipoSuscripcion> tipoSuscripcions=tipoSuscripcionServicio.listarTodas();
        if(tipoSuscripcions.isEmpty()){
            return ResponseEntity.ofNullable(new ApiResponse(false,"No existen suscripciones disponibles"));
        }

        return ResponseEntity.ok(new ApiResponse(true,"Consulta exitosa",tipoSuscripcions)) ;
    }
}
