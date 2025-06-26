package com.nicoGuti.optica.controlador;

import com.nicoGuti.optica.modelo.Anteojo;
import com.nicoGuti.optica.servicio.AnteojoServicio;
import com.nicoGuti.optica.util.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/anteojos")
public class AnteojoControlador {

    private final AnteojoServicio anteojoServicio;

    public AnteojoControlador(AnteojoServicio anteojoServicio) {
        this.anteojoServicio = anteojoServicio;
    }

    // Obtener todos los anteojos
    @GetMapping
    public ResponseEntity<?> obtenerTodos() {
        List<Anteojo> anteojos=anteojoServicio.obtenerTodos();

        if(anteojos.isEmpty()){

            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ApiResponse(true, "No se encontraron resultados"));

        }
        return ResponseEntity.ok(new ApiResponse(true, anteojos));



    }

    // Obtener anteojos por óptica
    @GetMapping("/optica/{opticaId}")
    public ResponseEntity<?> obtenerPorOptica(@PathVariable Long opticaId) {
        List<Anteojo> anteojos = anteojoServicio.obtenerPorOptica(opticaId);

        if (anteojos.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .body(new ApiResponse(true, "No se encontraron anteojos para esta óptica."));
        }

        return ResponseEntity.ok(new ApiResponse(true, anteojos));
    }

    // Crear nuevo anteojo
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody @Valid Anteojo anteojo, BindingResult result) {

        // Validaciones automáticas basadas en anotaciones
        if (result.hasErrors()) {
            List<String> errores = result.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .toList();

            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(false, "Errores de validación", errores));
        }

        // Guardar el anteojo si no hubo errores
        Anteojo guardado = anteojoServicio.guardar(anteojo);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse(true, guardado));
    }

    // Eliminar anteojo
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (!anteojoServicio.existePorId(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "No existe un anteojo con ID " + id));
        }

        anteojoServicio.eliminar(id);
        return ResponseEntity.ok(new ApiResponse(true, "Anteojo eliminado correctamente."));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody @Valid Anteojo anteojo, BindingResult result) {

        if (result.hasErrors()) {
            List<String> errores = result.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .toList();

            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Errores de validación", null, errores));
        }

        if (!anteojoServicio.buscarPorId(id).isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "No se encontró un anteojo con ID " + id));
        }

        anteojo.setId(id);
        Anteojo actualizado = anteojoServicio.guardar(anteojo);

        return ResponseEntity.ok(new ApiResponse(true, "Anteojo actualizado correctamente", actualizado));
    }

}
