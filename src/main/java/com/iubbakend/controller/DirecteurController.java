package com.iubbakend.controller;

import com.iubbakend.entity.Directeur;
import com.iubbakend.service.DirecteurService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/directeur")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${cors.allowed-origins}")
public class DirecteurController {
	private final DirecteurService service;

	@GetMapping
	public ResponseEntity<List<Directeur>> getAllDirecteurs() {
		log.info("GET /directeur - Récupération de tous les directeurs");
		List<Directeur> directeurs = service.findAll();
		return ResponseEntity.ok(directeurs);
	}

	@GetMapping("/current")
	public ResponseEntity<Directeur> getCurrentDirecteur() {
		log.info("GET /directeur/current - Récupération du directeur actuel");
		return service.findCurrentDirecteur()
				.map(directeur -> ResponseEntity.ok(directeur))
				.orElse(ResponseEntity.notFound().build());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Directeur> getDirecteurById(@PathVariable Long id) {
		log.info("GET /directeur/{} - Récupération directeur par ID", id);
		return service.findById(id)
				.map(directeur -> ResponseEntity.ok(directeur))
				.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<Directeur> createDirecteur(@Valid @RequestBody Directeur directeur) {
		log.info("POST /directeur - Création nouveau directeur: {}", directeur.getNom());
		Directeur savedDirecteur = service.save(directeur);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedDirecteur);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Directeur> updateDirecteur(@PathVariable Long id,
	                                                 @Valid @RequestBody Directeur directeur) {
		log.info("PUT /directeur/{} - Mise à jour directeur", id);
		try {
			Directeur updatedDirecteur = service.update(id, directeur);
			return ResponseEntity.ok(updatedDirecteur);
		} catch (RuntimeException e) {
			log.error("Erreur mise à jour directeur ID {}: {}", id, e.getMessage());
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteDirecteur(@PathVariable Long id) {
		log.info("DELETE /directeur/{} - Suppression directeur", id);
		try {
			service.deleteById(id);
			return ResponseEntity.noContent().build();
		} catch (RuntimeException e) {
			log.error("Erreur suppression directeur ID {}: {}", id, e.getMessage());
			return ResponseEntity.notFound().build();
		}
	}
}
