package com.nicoGuti.optica.servicio;

import com.nicoGuti.optica.modelo.Optica;
import com.nicoGuti.optica.repositorio.OpticaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service

public class ArchivoStorageService {
    private static final String DIRECTORIO_UPLOADS = "uploads";
    @Autowired
    private OpticaRepositorio opticaRepository;
    public String guardarArchivo(MultipartFile archivo, String subcarpeta) throws IOException {
        // Crear la carpeta con subcarpeta si no existe
        Path uploadPath = Paths.get(DIRECTORIO_UPLOADS, subcarpeta);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generar nombre único para evitar conflictos
        String nombreOriginal = archivo.getOriginalFilename();
        String extension = nombreOriginal != null && nombreOriginal.contains(".")
                ? nombreOriginal.substring(nombreOriginal.lastIndexOf("."))
                : "";
        String nombreArchivo = UUID.randomUUID() + extension;

        // Guardar el archivo
        Path archivoPath = uploadPath.resolve(nombreArchivo);
        Files.copy(archivo.getInputStream(), archivoPath, StandardCopyOption.REPLACE_EXISTING);

        // Devolver la ruta relativa completa (ej: uploads/opticas/uuid.png)
        return Paths.get(DIRECTORIO_UPLOADS, subcarpeta, nombreArchivo).toString();
    }
    public void eliminarArchivo(String pathRelativo) {
        try {
            // Elimina la primera barra si la tiene (ej: "/uploads/logos/archivo.png")
            Path pathAbsoluto = Paths.get("").toAbsolutePath().resolve(pathRelativo.replaceFirst("^/", ""));

            // Verifica que el path esté dentro de la carpeta 'uploads'
            if (!pathAbsoluto.toFile().getCanonicalPath().startsWith(new File("uploads").getCanonicalPath())) {
                throw new SecurityException("Intento de eliminar fuera del directorio permitido");
            }

            Files.deleteIfExists(pathAbsoluto);
            System.out.println("✅ Archivo eliminado correctamente:"+ pathAbsoluto);
        } catch (Exception e) {
            System.out.println("⚠️ No se pudo eliminar el archivo: " + pathRelativo+"/"+ e);
        }
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void limpiarLogosHuerfanosProgramado() {
        System.out.println("Ejecutando limpieza automática de logos huérfanos...");
        eliminarLogosHuerfanos();
    }

    public void eliminarLogosHuerfanos() {
        try {
            Path uploadPath = Paths.get(DIRECTORIO_UPLOADS);
            if (!Files.exists(uploadPath)) {
                System.out.println("Carpeta uploads no existe.");
                return;
            }

            List<String> archivosEnDisco = Files.list(uploadPath)
                    .map(path -> path.getFileName().toString())
                    .collect(Collectors.toList());

            List<String> logosUsados = opticaRepository.findAll().stream()
                    .map(Optica::getLogoUrl)
                    .filter(Objects::nonNull)
                    .map(logo -> Paths.get(logo).getFileName().toString())
                    .collect(Collectors.toList());

            List<String> archivosHuerfanos = archivosEnDisco.stream()
                    .filter(nombre -> !logosUsados.contains(nombre))
                    .collect(Collectors.toList());

            for (String nombre : archivosHuerfanos) {
                Path pathCompleto = uploadPath.resolve(nombre);
                Files.deleteIfExists(pathCompleto);
                System.out.println("Eliminado logo huérfano: " + nombre);
            }

            System.out.println("Limpieza de logos huérfanos finalizada.");

        } catch (Exception e) {
            System.out.println("Error al limpiar logos huérfanos: " + e.getMessage());
            e.printStackTrace();
        }
    }


}
