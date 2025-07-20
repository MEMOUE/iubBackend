package com.iubbakend.controller;

import com.iubbakend.entity.Formation;
import com.iubbakend.service.FormationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/formations")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${cors.allowed-origins}")
public class FormationController {
	private final FormationService service;

	@GetMapping
	public ResponseEntity<List<Formation>> getAllFormations() {
		log.info("GET /formations - Récupération de toutes les formations");
		List<Formation> formations = service.findAll();
		return ResponseEntity.ok(formations);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Formation> getFormationById(@PathVariable Long id) {
		log.info("GET /formations/{} - Récupération formation par ID", id);
		return service.findById(id)
				.map(formation -> ResponseEntity.ok(formation))
				.orElse(ResponseEntity.notFound().build());
	}

	@GetMapping("/categorie/{categorie}")
	public ResponseEntity<List<Formation>> getFormationsByCategorie(@PathVariable String categorie) {
		log.info("GET /formations/categorie/{} - Récupération formations par catégorie", categorie);
		List<Formation> formations = service.findByCategorie(categorie);
		return ResponseEntity.ok(formations);
	}

	@GetMapping("/search")
	public ResponseEntity<List<Formation>> searchFormations(@RequestParam String keyword) {
		log.info("GET /formations/search?keyword={} - Recherche formations", keyword);
		List<Formation> formations = service.searchByKeyword(keyword);
		return ResponseEntity.ok(formations);
	}

	@GetMapping("/count")
	public ResponseEntity<Long> countFormations() {
		log.info("GET /formations/count - Comptage formations");
		long count = service.count();
		return ResponseEntity.ok(count);
	}

	@PostMapping
	public ResponseEntity<Formation> createFormation(@Valid @RequestBody Formation formation) {
		log.info("POST /formations - Création nouvelle formation: {}", formation.getNom());
		Formation savedFormation = service.save(formation);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedFormation);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Formation> updateFormation(@PathVariable Long id,
	                                                 @Valid @RequestBody Formation formation) {
		log.info("PUT /formations/{} - Mise à jour formation", id);
		try {
			Formation updatedFormation = service.update(id, formation);
			return ResponseEntity.ok(updatedFormation);
		} catch (RuntimeException e) {
			log.error("Erreur mise à jour formation ID {}: {}", id, e.getMessage());
			return ResponseEntity.notFound().build();
		}
	}

	@PutMapping("/{id}/inscrire")
	public ResponseEntity<Formation> inscrireEtudiant(@PathVariable Long id) {
		log.info("PUT /formations/{}/inscrire - Inscription étudiant", id);
		try {
			Formation formation = service.inscrireEtudiant(id);
			return ResponseEntity.ok(formation);
		} catch (RuntimeException e) {
			log.error("Erreur inscription formation ID {}: {}", id, e.getMessage());
			return ResponseEntity.badRequest().build();
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteFormation(@PathVariable Long id) {
		log.info("DELETE /formations/{} - Suppression formation", id);
		try {
			service.deleteById(id);
			return ResponseEntity.noContent().build();
		} catch (RuntimeException e) {
			log.error("Erreur suppression formation ID {}: {}", id, e.getMessage());
			return ResponseEntity.notFound().build();
		}
	}
}
