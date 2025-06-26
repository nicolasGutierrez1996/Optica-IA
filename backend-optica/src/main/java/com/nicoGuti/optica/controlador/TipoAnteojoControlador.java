package com.nicoGuti.optica.controlador;

import com.nicoGuti.optica.modelo.TipoAnteojo;
import com.nicoGuti.optica.servicio.TipoAnteojoServicio;
import com.nicoGuti.optica.util.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tipos-anteojo")
public class TipoAnteojoControlador {
    @Autowired
    private TipoAnteojoServicio tipoAnteojoService;

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody @Valid TipoAnteojo tipo, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errores = result.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .toList();

            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Errores de validación", null, errores));
        }

        return ResponseEntity.ok(new ApiResponse(true, tipoAnteojoService.guardar(tipo)));
    }

    @GetMapping
    public ResponseEntity<?> listar() {
        return ResponseEntity.ok(new ApiResponse(true, tipoAnteojoService.listarTodos()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        return tipoAnteojoService.buscarPorId(id)
                .map(tipo -> ResponseEntity.ok(new ApiResponse(true, tipo)))
                .orElseGet(() -> ResponseEntity.status(404)
                        .body(new ApiResponse(false, "Tipo de anteojo no encontrado")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        tipoAnteojoService.eliminar(id);
        return ResponseEntity.ok(new ApiResponse(true, "Tipo de anteojo eliminado"));
    }
}
