package com.nicoGuti.optica.controlador;

import com.nicoGuti.optica.modelo.Optica;
import com.nicoGuti.optica.servicio.OpticaServicio;
import com.nicoGuti.optica.util.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/opticas")
public class OpticaControlador {

    private final OpticaServicio opticaServicio;

    public OpticaControlador(OpticaServicio opticaServicio) {
        this.opticaServicio = opticaServicio;
    }

    @GetMapping
    public ResponseEntity<?> obtenerTodas() {
        List<Optica> opticas = opticaServicio.obtenerTodas();

        if (opticas.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new ApiResponse(true, "No se encontraron ópticas registradas."));
        }

        return ResponseEntity.ok(new ApiResponse(true, opticas));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        Optional<Optica> optica = opticaServicio.buscarPorId(id);

        if (optica.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "No se encontró una óptica con ID " + id));
        }

        return ResponseEntity.ok(new ApiResponse(true, optica.get()));
    }

    @PostMapping
    public ResponseEntity<?> guardar(@RequestBody @Valid Optica optica, BindingResult result) {

        // Validaciones automáticas de Bean Validation
        if (result.hasErrors()) {
            List<String> errores = result.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .toList();

            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Errores de validación", null, errores));
        }

        Optica guardada = opticaServicio.guardar(optica);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "Óptica guardada correctamente", guardada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (opticaServicio.buscarPorId(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "No existe una óptica con ID " + id));
        }

        opticaServicio.eliminar(id);
        return ResponseEntity.ok(new ApiResponse(true, "Óptica eliminada correctamente."));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<?> buscarPorEmail(@PathVariable String email) {
        Optional<Optica> optica = opticaServicio.buscarPorEmail(email);

        if (optica.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "No se encontró una óptica con email " + email));
        }

        return ResponseEntity.ok(new ApiResponse(true, optica.get()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody @Valid Optica optica, BindingResult result) {

        // Validaciones automáticas (nombre, email, etc.)
        if (result.hasErrors()) {
            List<String> errores = result.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .toList();

            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Errores de validación", null, errores));
        }

        // Validación de existencia previa
        if (!opticaServicio.buscarPorId(id).isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "No se encontró una óptica con ID " + id));
        }

        optica.setId(id);
        Optica actualizada = opticaServicio.guardar(optica);

        return ResponseEntity.ok(new ApiResponse(true, "Óptica actualizada correctamente", actualizada));
    }
}
