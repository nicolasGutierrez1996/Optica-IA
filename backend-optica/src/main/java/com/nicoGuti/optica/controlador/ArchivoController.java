package com.nicoGuti.optica.controlador;

import com.nicoGuti.optica.util.ApiResponse;
import com.nicoGuti.optica.util.ReplicateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/api/archivos")
public class ArchivoController {

    @Autowired
    private ReplicateService replicateService;

    @PostMapping("/upload-imagen")
    public ResponseEntity<?> uploadImagen(
            @RequestParam("file") MultipartFile file,
            @RequestParam("tipo") String tipo) {

        try {
            // Validar tipo
            if (!tipo.equals("anteojo") && !tipo.equals("optica")) {

                return ResponseEntity.badRequest().body(new ApiResponse(false,"Tipo no válido: debe ser anteojo u optica"));
            }

            // Crear nombre de archivo único
            String nombreArchivo = UUID.randomUUID() + "_" + file.getOriginalFilename();

            // Definir carpeta según el tipo
            String carpeta = tipo.equals("optica") ? "uploads/opticas/" : "uploads/anteojos/";
            Path rutaDestino = Paths.get(carpeta);

            // Crear la carpeta si no existe
            if (!Files.exists(rutaDestino)) {
                Files.createDirectories(rutaDestino);
            }

            // Guardar el archivo
            Path rutaArchivo = rutaDestino.resolve(nombreArchivo);
            Files.copy(file.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);

            // Retornar la URL relativa para usar en la base de datos o front
            return ResponseEntity.ok(new ApiResponse(true,"/" + carpeta + nombreArchivo));

        } catch (IOException e) {


            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false,"Error al subir la imagen"));

        }
    }

    @DeleteMapping("/delete-imagen")
    public ResponseEntity<?> eliminarImagen(@RequestParam("path") String pathRelativo) {
        try {
            Path pathAbsoluto = Paths.get("").toAbsolutePath().resolve(pathRelativo.substring(1)); // Quita la primera barra
            if (!pathAbsoluto.toFile().getCanonicalPath().startsWith(new File("uploads").getCanonicalPath())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse(false,"No autorizado"));
            }



            Files.deleteIfExists(pathAbsoluto);
            return ResponseEntity.ok(new ApiResponse(true,"Imagen eliminada con exito"));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false,"Error al eliminar la imagen"));
        }
    }





    @PostMapping("/generar-superposicion")
    public ResponseEntity<?> generarSuperposicion(
            @RequestParam("fotoCliente") MultipartFile fotoCliente) {

        try {
            String base64Cliente = Base64.getEncoder().encodeToString(fotoCliente.getBytes());

            byte[] imagenFinal = replicateService.generarSuperposicion(base64Cliente, "with eyeglasses, realistic style");

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(imagenFinal);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false,"Fallo la generacion de la imagen superpuesta"));
        }
    }

}