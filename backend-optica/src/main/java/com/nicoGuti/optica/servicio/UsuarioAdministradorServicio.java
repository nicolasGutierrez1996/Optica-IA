package com.nicoGuti.optica.servicio;

import com.nicoGuti.optica.modelo.Optica;
import com.nicoGuti.optica.modelo.UsuarioAdministrador;
import com.nicoGuti.optica.repositorio.UsuarioAdministradorRepositorio;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioAdministradorServicio {

    private final UsuarioAdministradorRepositorio usuarioRepo;

    public UsuarioAdministradorServicio(UsuarioAdministradorRepositorio usuarioRepo) {
        this.usuarioRepo = usuarioRepo;
    }

    public List<UsuarioAdministrador> obtenerTodos() {
        return usuarioRepo.findAll();
    }

    public Optional<UsuarioAdministrador> buscarPorId(Long id) {
        return usuarioRepo.findById(id);
    }

    public UsuarioAdministrador guardar(UsuarioAdministrador admin) {
        return usuarioRepo.save(admin);
    }

    public void eliminar(Long id) {
        usuarioRepo.deleteById(id);
    }

    public Optional<UsuarioAdministrador> buscarPorUsername(String username) {
        return usuarioRepo.findByUsername(username);
    }

    public Optional<UsuarioAdministrador> buscarPorEmail(String email) {
        return usuarioRepo.findByEmail(email);
    }

    public List<UsuarioAdministrador> buscarPorOpticaId(Long opticaId) {
        return usuarioRepo.findByOpticaId(opticaId);
    }

    public Optional<String> obtenerTokenPorMail(String mail){
        return usuarioRepo.findTokenByEmail(mail);
    }

    public void actualizarPassword(String email, String nuevaPassword) {
        UsuarioAdministrador usuario = usuarioRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setPassword(nuevaPassword);
        usuarioRepo.save(usuario);
    }

    public Optional<UsuarioAdministrador> buscarPorMail(String email){
        return usuarioRepo.findByEmail(email);

    }

    public boolean existeUserName(String userName){
        return usuarioRepo.existsByUsername(userName);
    }

    public boolean existeEmail(String email){
        return usuarioRepo.existsByEmail(email);
    }

    public Optional<Boolean> esUsuarioVerificado(String username){
        return usuarioRepo.esUsuarioVerificado(username);
    }

    public Optional<UsuarioAdministrador> buscarUserPorOptica(Optica optica){
        return usuarioRepo.findByOptica(optica);
    }
    public Optional<UsuarioAdministrador> buscarPorDni(String dni){
        return usuarioRepo.findByDni(dni);
    }

    public Optional<String> buscarContraseniaPorId(Long id){
        return usuarioRepo.findPasswordById(id);
    }



}
