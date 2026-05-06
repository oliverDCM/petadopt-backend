package com.oliest.backend.controller;

import com.oliest.backend.entity.Animal;
import com.oliest.backend.service.AnimalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/animales")
@CrossOrigin(origins = "*")
public class AnimalController {

    @Autowired
    private AnimalService animalService;

    @PostMapping("/publicar")
    public ResponseEntity<?> publicar(@RequestBody Animal animal, Principal principal) {
        try {
            Animal nuevo = animalService.publicar(animal, principal.getName());
            return ResponseEntity.ok(nuevo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/venta")
    public ResponseEntity<List<Animal>> getEnVenta() {
        return ResponseEntity.ok(animalService.getEnVenta());
    }

    @GetMapping("/adopcion")
    public ResponseEntity<List<Animal>> getEnAdopcion() {
        return ResponseEntity.ok(animalService.getEnAdopcion());
    }

    @GetMapping("/mis-animales")
    public ResponseEntity<List<Animal>> getMisAnimales(Principal principal) {
        return ResponseEntity.ok(animalService.getMisAnimales(principal.getName()));
    }

    // NUEVO: Editar un animal propio
    @PutMapping("/editar/{id}")
    public ResponseEntity<?> editar(@PathVariable Long id,
                                    @RequestBody Animal datos,
                                    Principal principal) {
        try {
            Animal actualizado = animalService.editarAnimal(id, datos, principal.getName());
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // NUEVO: Eliminar un animal propio
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id, Principal principal) {
        try {
            animalService.eliminarAnimal(id, principal.getName());
            return ResponseEntity.ok("Animal eliminado correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
