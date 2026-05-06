package com.oliest.backend.repository;

import com.oliest.backend.entity.Animal;
import com.oliest.backend.entity.TipoPublicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Long> {
    List<Animal> findByTipo(TipoPublicacion tipo);
    List<Animal> findByPropietarioId(Long usuarioId);
}