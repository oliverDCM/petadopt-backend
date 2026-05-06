package com.oliest.backend.controller;

import com.oliest.backend.entity.Solicitud;
import com.oliest.backend.service.SolicitudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/solicitudes")
@CrossOrigin(origins = "*")
public class SolicitudController {

    @Autowired
    private SolicitudService solicitudService;

    // Enviar solicitud con mensaje/descripción
    @PostMapping("/enviar/{animalId}")
    public ResponseEntity<?> enviar(@PathVariable Long animalId,
                                    @RequestBody Map<String, String> body,
                                    Principal principal) {
        try {
            Solicitud solicitud = solicitudService.enviarSolicitud(
                    animalId,
                    body.get("mensaje"),
                    principal.getName()
            );
            return ResponseEntity.ok(solicitud);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Solicitudes recibidas (propietario del animal)
    @GetMapping("/recibidas")
    public ResponseEntity<List<Solicitud>> getRecibidas(Principal principal) {
        return ResponseEntity.ok(solicitudService.getSolicitudesRecibidas(principal.getName()));
    }

    // Mis solicitudes enviadas (adoptante)
    @GetMapping("/mis-solicitudes")
    public ResponseEntity<List<Solicitud>> getMias(Principal principal) {
        return ResponseEntity.ok(solicitudService.getMisSolicitudes(principal.getName()));
    }

    // Responder solicitud (aprobar o rechazar)
    @PutMapping("/responder/{solicitudId}")
    public ResponseEntity<?> responder(@PathVariable Long solicitudId,
                                       @RequestBody Map<String, String> body,
                                       Principal principal) {
        try {
            Solicitud solicitud = solicitudService.responderSolicitud(
                    solicitudId,
                    body.get("estado"),
                    principal.getName()
            );
            return ResponseEntity.ok(solicitud);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // NUEVO: Cancelar solicitud (solo el que la envió y solo si está PENDIENTE)
    @DeleteMapping("/cancelar/{solicitudId}")
    public ResponseEntity<?> cancelar(@PathVariable Long solicitudId, Principal principal) {
        try {
            solicitudService.cancelarSolicitud(solicitudId, principal.getName());
            return ResponseEntity.ok("Solicitud cancelada correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
