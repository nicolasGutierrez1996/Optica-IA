package com.nicoGuti.optica.controlador;

import com.nicoGuti.optica.modelo.Direccion;
import com.nicoGuti.optica.servicio.DireccionServicio;
import com.nicoGuti.optica.util.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/direcciones")
public class DireccionControlador {

    @Autowired
    private DireccionServicio direccionServicio;

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody @Valid Direccion direccion, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errores = result.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .toList();

            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Errores de validación", null, errores));
        }

        return ResponseEntity.ok(new ApiResponse(true, direccionServicio.guardar(direccion)));
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        Optional<Direccion> dir = direccionServicio.buscarPorId(id);
        return dir.isPresent()
                ? ResponseEntity.ok(new ApiResponse(true, dir.get()))
                : ResponseEntity.status(404).body(new ApiResponse(false, "Dirección no encontrada"));
    }

    @GetMapping
    public ResponseEntity<?> listar() {
        return ResponseEntity.ok(new ApiResponse(true, direccionServicio.listarTodas()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        direccionServicio.eliminar(id);
        return ResponseEntity.ok(new ApiResponse(true, "Dirección eliminada"));
    }
}
