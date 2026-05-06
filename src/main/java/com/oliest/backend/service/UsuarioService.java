package com.oliest.backend.service;

import com.oliest.backend.dto.LoginRequest;
import com.oliest.backend.dto.LoginResponse;
import com.oliest.backend.dto.RegistroRequest;
import com.oliest.backend.entity.Rol;
import com.oliest.backend.entity.Usuario;
import com.oliest.backend.repository.AnimalRepository;
import com.oliest.backend.repository.SolicitudRepository;
import com.oliest.backend.repository.UsuarioRepository;
import com.oliest.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private SolicitudRepository solicitudRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public Usuario registrar(RegistroRequest request) {
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("El email ya está registrado");
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setTelefono(request.getTelefono());
        usuario.setCiudad(request.getCiudad());
        usuario.setRol(Rol.valueOf(request.getRol().toUpperCase()));
        return usuarioRepository.save(usuario);
    }

    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }
        String token = jwtUtil.generateToken(usuario.getEmail(), usuario.getRol().name());
        return new LoginResponse(token, usuario.getNombre(), usuario.getEmail(), usuario.getRol().name());
    }

    public Usuario getPerfil(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    // NUEVO: editar nombre, telefono y ciudad
    public Usuario editarPerfil(String email, String nombre, String telefono, String ciudad) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if (nombre  != null && !nombre.isBlank())   usuario.setNombre(nombre);
        if (telefono!= null && !telefono.isBlank())  usuario.setTelefono(telefono);
        if (ciudad  != null && !ciudad.isBlank())    usuario.setCiudad(ciudad);
        return usuarioRepository.save(usuario);
    }

    // Eliminar cuenta junto con sus animales y solicitudes
    public void eliminarCuenta(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Eliminar solicitudes enviadas por el usuario
        solicitudRepository.findBySolicitanteId(usuario.getId())
                .forEach(s -> solicitudRepository.delete(s));

        // Eliminar solicitudes recibidas en sus animales y luego los animales
        animalRepository.findByPropietarioId(usuario.getId()).forEach(animal -> {
            solicitudRepository.deleteByAnimalId(animal.getId());
            animalRepository.delete(animal);
        });

        usuarioRepository.delete(usuario);
    }
}
