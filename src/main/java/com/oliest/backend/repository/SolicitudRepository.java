package com.oliest.backend.repository;

import com.oliest.backend.entity.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {
    List<Solicitud> findByAnimalPropietarioId(Long propietarioId);
    List<Solicitud> findBySolicitanteId(Long solicitanteId);
    List<Solicitud> findByAnimalId(Long animalId);
    // NUEVO: para borrar solicitudes al eliminar un animal
    void deleteByAnimalId(Long animalId);
}
