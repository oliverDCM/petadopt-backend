package com.oliest.backend.controller;

import com.oliest.backend.dto.LoginRequest;
import com.oliest.backend.dto.LoginResponse;
import com.oliest.backend.dto.RegistroRequest;
import com.oliest.backend.entity.Usuario;
import com.oliest.backend.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody RegistroRequest request) {
        try {
            Usuario nuevo = usuarioService.registrar(request);
            return ResponseEntity.ok(nuevo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = usuarioService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Ver mi perfil
    @GetMapping("/perfil")
    public ResponseEntity<?> getPerfil(Principal principal) {
        try {
            Usuario usuario = usuarioService.getPerfil(principal.getName());
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // NUEVO: Editar perfil (nombre, telefono, ciudad)
    @PutMapping("/editar")
    public ResponseEntity<?> editar(@RequestBody Map<String, String> body, Principal principal) {
        try {
            Usuario actualizado = usuarioService.editarPerfil(
                    principal.getName(),
                    body.get("nombre"),
                    body.get("telefono"),
                    body.get("ciudad")
            );
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/eliminar")
    public ResponseEntity<?> eliminar(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("Usuario no autenticado");
        }
        try {
            usuarioService.eliminarCuenta(principal.getName());
            return ResponseEntity.ok("Cuenta eliminada correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
