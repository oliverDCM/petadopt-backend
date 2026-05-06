package com.oliest.backend.service;

import com.oliest.backend.entity.Animal;
import com.oliest.backend.entity.TipoPublicacion;
import com.oliest.backend.entity.Usuario;
import com.oliest.backend.repository.AnimalRepository;
import com.oliest.backend.repository.SolicitudRepository;
import com.oliest.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnimalService {

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private SolicitudRepository solicitudRepository;

    public Animal publicar(Animal animal, String emailPropietario) {
        Usuario propietario = usuarioRepository.findByEmail(emailPropietario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        animal.setPropietario(propietario);
        return animalRepository.save(animal);
    }

    public List<Animal> getEnVenta() {
        return animalRepository.findByTipo(TipoPublicacion.VENTA);
    }

    public List<Animal> getEnAdopcion() {
        return animalRepository.findByTipo(TipoPublicacion.ADOPCION);
    }

    public List<Animal> getMisAnimales(String emailPropietario) {
        Usuario propietario = usuarioRepository.findByEmail(emailPropietario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return animalRepository.findByPropietarioId(propietario.getId());
    }

    // NUEVO: editar animal verificando que sea el propietario
    public Animal editarAnimal(Long id, Animal datos, String emailPropietario) {
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Animal no encontrado"));

        if (!animal.getPropietario().getEmail().equals(emailPropietario)) {
            throw new RuntimeException("No tienes permiso para editar este animal");
        }

        animal.setNombre(datos.getNombre());
        animal.setEspecie(datos.getEspecie());
        animal.setRaza(datos.getRaza());
        animal.setEdad(datos.getEdad());
        animal.setDescripcion(datos.getDescripcion());
        animal.setVacunado(datos.isVacunado());
        animal.setEsterilizado(datos.isEsterilizado());
        animal.setTipo(datos.getTipo());

        return animalRepository.save(animal);
    }

    // NUEVO: eliminar animal y sus solicitudes primero
    public void eliminarAnimal(Long id, String emailPropietario) {
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Animal no encontrado"));

        if (!animal.getPropietario().getEmail().equals(emailPropietario)) {
            throw new RuntimeException("No tienes permiso para eliminar este animal");
        }

        // Eliminar solicitudes asociadas antes de eliminar el animal
        solicitudRepository.deleteByAnimalId(id);
        animalRepository.delete(animal);
    }
}
