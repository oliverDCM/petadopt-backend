package com.oliest.backend.service;

import com.oliest.backend.entity.*;
import com.oliest.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SolicitudService {

    @Autowired
    private SolicitudRepository solicitudRepository;

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Solicitud enviarSolicitud(Long animalId, String mensaje, String emailSolicitante) {
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new RuntimeException("Animal no encontrado"));

        Usuario solicitante = usuarioRepository.findByEmail(emailSolicitante)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Evitar solicitud duplicada pendiente
        boolean yaEnvio = solicitudRepository
                .findBySolicitanteId(solicitante.getId())
                .stream()
                .anyMatch(s -> s.getAnimal().getId().equals(animalId)
                        && s.getEstado() == EstadoSolicitud.PENDIENTE);
        if (yaEnvio) {
            throw new RuntimeException("Ya tienes una solicitud pendiente para este animal");
        }

        Solicitud solicitud = new Solicitud();
        solicitud.setAnimal(animal);
        solicitud.setSolicitante(solicitante);
        solicitud.setMensaje(mensaje);
        solicitud.setEstado(EstadoSolicitud.PENDIENTE);

        return solicitudRepository.save(solicitud);
    }

    public List<Solicitud> getSolicitudesRecibidas(String emailPropietario) {
        Usuario propietario = usuarioRepository.findByEmail(emailPropietario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return solicitudRepository.findByAnimalPropietarioId(propietario.getId());
    }

    public List<Solicitud> getMisSolicitudes(String emailSolicitante) {
        Usuario solicitante = usuarioRepository.findByEmail(emailSolicitante)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return solicitudRepository.findBySolicitanteId(solicitante.getId());
    }

    public Solicitud responderSolicitud(Long solicitudId, String estado, String emailPropietario) {
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        if (!solicitud.getAnimal().getPropietario().getEmail().equals(emailPropietario)) {
            throw new RuntimeException("No tienes permiso para responder esta solicitud");
        }

        solicitud.setEstado(EstadoSolicitud.valueOf(estado.toUpperCase()));
        return solicitudRepository.save(solicitud);
    }

    // NUEVO: Cancelar solicitud (solo el solicitante, solo si está PENDIENTE)
    public void cancelarSolicitud(Long solicitudId, String emailSolicitante) {
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        if (!solicitud.getSolicitante().getEmail().equals(emailSolicitante)) {
            throw new RuntimeException("No tienes permiso para cancelar esta solicitud");
        }

        if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE) {
            throw new RuntimeException("Solo puedes cancelar solicitudes en estado PENDIENTE");
        }

        solicitudRepository.delete(solicitud);
    }
}
