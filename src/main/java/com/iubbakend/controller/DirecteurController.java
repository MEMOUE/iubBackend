package com.iubbakend.controller;

import com.iubbakend.entity.Directeur;
import com.iubbakend.service.DirecteurService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/directeur")
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

	@GetMapping("/status")
	public ResponseEntity<Map<String, Object>> getDirecteurStatus() {
		log.info("GET /directeur/status - Récupération du statut des directeurs");

		Map<String, Object> status = new HashMap<>();
		status.put("existeDirecteurActif", service.existeDirecteurActif());
		status.put("nombreTotalDirecteurs", service.countAll());
		status.put("nombreDirecteursActifs", service.countActifs());

		return ResponseEntity.ok(status);
	}

	@PostMapping
	public ResponseEntity<?> createDirecteur(@Valid @RequestBody Directeur directeur) {
		log.info("POST /directeur - Création nouveau directeur: {}", directeur.getNom());

		try {
			// Vérifier s'il existe déjà un directeur actif
			if (service.existeDirecteurActif()) {
				log.warn("Tentative de création d'un directeur alors qu'un directeur est déjà actif");

				Map<String, Object> response = new HashMap<>();
				response.put("message", "Un directeur est déjà actif. Voulez-vous le remplacer ?");
				response.put("directeurActuel", service.findCurrentDirecteur().orElse(null));
				response.put("warning", true);

				return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
			}

			Directeur savedDirecteur = service.save(directeur);
			return ResponseEntity.status(HttpStatus.CREATED).body(savedDirecteur);

		} catch (Exception e) {
			log.error("Erreur lors de la création du directeur: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "Erreur lors de la création du directeur: " + e.getMessage()));
		}
	}

	@PostMapping("/force")
	public ResponseEntity<Directeur> forceCreateDirecteur(@Valid @RequestBody Directeur directeur) {
		log.info("POST /directeur/force - Création forcée nouveau directeur: {}", directeur.getNom());

		// Force la création même s'il existe déjà un directeur actif
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

	@PutMapping("/{id}/activer")
	public ResponseEntity<Directeur> activerDirecteur(@PathVariable Long id) {
		log.info("PUT /directeur/{}/activer - Activation directeur", id);
		try {
			Directeur directeur = service.activerDirecteur(id);
			return ResponseEntity.ok(directeur);
		} catch (RuntimeException e) {
			log.error("Erreur activation directeur ID {}: {}", id, e.getMessage());
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