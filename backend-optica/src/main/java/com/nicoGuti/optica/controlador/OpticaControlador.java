package com.nicoGuti.optica.controlador;

import com.nicoGuti.optica.modelo.*;
import com.nicoGuti.optica.servicio.*;
import com.nicoGuti.optica.util.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/opticas")
public class OpticaControlador {

    private final OpticaServicio opticaServicio;
    @Autowired
    private ArchivoStorageService archivoStorageService;
    @Autowired
    private UsuarioAdministradorServicio usuarioAdministradorServicio;

    @Autowired
    private SuscripcionServicio suscripcionServicio;

    @Autowired
    private CuponDescuentoServicio cuponDescuentoServicio;

    @Autowired
    private TokenVerificadorServicio tokenVerificadorServicio;

    @Autowired
    private DireccionServicio direccionServicio;

    @Autowired
    private AnteojoServicio anteojoServicio;





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
    public ResponseEntity<?> guardar(@RequestBody @Valid Optica optica,
                                     BindingResult result,
                                     Authentication authentication) {
        // 1. Validación de Bean Validation
        if (result.hasErrors()) {
            List<String> errores = result.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .toList();

            errores.forEach(err -> System.out.println(" - " + err));

            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Errores de validación", null, errores));
        }

        // 2. Validación de unicidad
        if (opticaServicio.existePorNombre(optica.getNombre())) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Ya existe una óptica con ese nombre"));
        }

        if (opticaServicio.existePorMail(optica.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Ya existe una óptica con ese email"));
        }

        // 3. Guardar óptica
        optica.setActivo(false);
        Optica guardada = opticaServicio.guardar(optica);
        UsuarioAdministrador user = (UsuarioAdministrador) authentication.getPrincipal();
        String username = user.getUsername();


        UsuarioAdministrador usuario = usuarioAdministradorServicio.buscarPorUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 5. Vincular la óptica
        usuario.setOptica(guardada);
        usuarioAdministradorServicio.guardar(usuario);

        // 6. Responder
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "Óptica guardada y vinculada correctamente", guardada));
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
    public ResponseEntity<?> actualizar(@PathVariable Long id,
                                        @RequestBody @Valid Optica opticaEditada,
                                        BindingResult result) {

        // 1. Validaciones del formulario
        if (result.hasErrors()) {
            List<String> errores = result.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .toList();
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Errores de validación", null, errores));
        }

        // 2. Buscar la óptica original
        Optica opticaOriginal = opticaServicio.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("No se encontró una óptica con ID " + id));

        // 3. Validar nombre único (si cambió)
        if (!opticaOriginal.getNombre().equals(opticaEditada.getNombre())
                && opticaServicio.existePorNombre(opticaEditada.getNombre())) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Ya existe una óptica con ese nombre"));
        }

        // 4. Validar email único (si cambió)
        if (!opticaOriginal.getEmail().equals(opticaEditada.getEmail())
                && opticaServicio.existePorMail(opticaEditada.getEmail())) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Ya existe una óptica con ese email"));
        }

        // 5. Eliminar logo anterior si fue reemplazado
        if (logoCambio(opticaOriginal.getLogoUrl(), opticaEditada.getLogoUrl())) {
            archivoStorageService.eliminarArchivo(opticaOriginal.getLogoUrl());
        }

        opticaOriginal.setNombre(opticaEditada.getNombre());
        opticaOriginal.setEmail(opticaEditada.getEmail());
        opticaOriginal.setTelefono(opticaEditada.getTelefono());
        opticaOriginal.setDescripcion(opticaEditada.getDescripcion());
        opticaOriginal.setLogoUrl(opticaEditada.getLogoUrl());
        opticaOriginal.setDireccion(opticaEditada.getDireccion());

        // 7. Guardar
        Optica actualizada = opticaServicio.guardar(opticaOriginal);

        return ResponseEntity.ok(new ApiResponse(true, "Óptica actualizada correctamente", actualizada));
    }
    private boolean logoCambio(String anterior, String nuevo) {
        return anterior != null && nuevo != null && !anterior.equals(nuevo);
    }

    @PostMapping("/upload-logo")
    public ResponseEntity<?> subirLogo(@RequestParam("archivo") MultipartFile archivo) {
        try {
            // Guardar el archivo en la subcarpeta "opticas"
            String nombreArchivo = archivoStorageService.guardarArchivo(archivo, "optica");
            String url = nombreArchivo.replace("\\", "/");

            return ResponseEntity.ok(new ApiResponse(true, "Logo subido correctamente", url));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error al subir el logo"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarOptica(@PathVariable Long id) {
        Optional<Optica> opticaOp = opticaServicio.buscarPorId(id);
        if (opticaOp.isEmpty()) {
            return ResponseEntity.ok(new ApiResponse(false, "La óptica que se desea eliminar no existe"));
        }

        Optica optica = opticaOp.get();

        Optional<UsuarioAdministrador> usuarioOp = usuarioAdministradorServicio.buscarUserPorOptica(optica);
        if (usuarioOp.isEmpty()) {
            return ResponseEntity.ok(new ApiResponse(false, "No se encontró el usuario asociado a la óptica"));
        }

        UsuarioAdministrador usuario = usuarioOp.get();

        try {
            // 1. Eliminar token_verificacion (si existe)
            if (usuario.getToken() != null && !usuario.getToken().isBlank()) {
                tokenVerificadorServicio.deleteByUsuario(usuario);
            }

            // 2. Eliminar suscripción (si existe)
            List<Suscripcion> suscripciones = suscripcionServicio.buscarTodasPorUsuario(usuario.getId());
            for (Suscripcion s : suscripciones) {
                suscripcionServicio.eliminar(s.getId());
            }

            // 3. Eliminar anteojos vinculados a la óptica
            List<Anteojo> anteojos = anteojoServicio.obtenerPorOptica(optica.getId());
            for (Anteojo a : anteojos) {
                anteojoServicio.eliminar(a.getId());
            }

            // 4. Eliminar cupon creado por la óptica (si existe)
            Optional<CuponDescuento> cuponOp = cuponDescuentoServicio.buscarPorOpticaId(optica.getId());
            cuponOp.ifPresent(c -> cuponDescuentoServicio.eliminar(c.getId()));

            // 5. Desvincular relaciones antes de eliminar óptica
            usuario.setOptica(null);
            usuarioAdministradorServicio.guardar(usuario);

            opticaServicio.guardar(optica);

            // 6. Eliminar óptica
            opticaServicio.eliminar(optica.getId());

            // 7. Eliminar dirección (ya no referenciada)
            if (optica.getDireccion() != null) {
                direccionServicio.eliminar(optica.getDireccion().getId());
            }

            // 8. Finalmente eliminar usuario
            usuarioAdministradorServicio.eliminar(usuario.getId());

            return ResponseEntity.ok(new ApiResponse(true, "Óptica, usuario y datos asociados eliminados correctamente"));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse(false, "Error al eliminar la óptica: " + e.getMessage()));
        }
    }



}




