package com.iubbakend.controller;

import com.iubbakend.entity.EntreprisePartenaire;
import com.iubbakend.service.EntreprisePartenaireService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/entreprises-partenaires")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${cors.allowed-origins}")
public class EntreprisePartenaireController {
	private final EntreprisePartenaireService service;

	@GetMapping
	public ResponseEntity<List<EntreprisePartenaire>> getAllEntreprises() {
		log.info("GET /entreprises-partenaires - Récupération de toutes les entreprises");
		List<EntreprisePartenaire> entreprises = service.findAll();
		return ResponseEntity.ok(entreprises);
	}

	@GetMapping("/{id}")
	public ResponseEntity<EntreprisePartenaire> getEntrepriseById(@PathVariable Long id) {
		log.info("GET /entreprises-partenaires/{} - Récupération entreprise par ID", id);
		return service.findById(id)
				.map(entreprise -> ResponseEntity.ok(entreprise))
				.orElse(ResponseEntity.notFound().build());
	}

	@GetMapping("/secteur/{secteur}")
	public ResponseEntity<List<EntreprisePartenaire>> getEntreprisesBySecteur(@PathVariable String secteur) {
		log.info("GET /entreprises-partenaires/secteur/{} - Récupération entreprises par secteur", secteur);
		List<EntreprisePartenaire> entreprises = service.findBySecteur(secteur);
		return ResponseEntity.ok(entreprises);
	}

	@GetMapping("/search")
	public ResponseEntity<List<EntreprisePartenaire>> searchEntreprises(@RequestParam String keyword) {
		log.info("GET /entreprises-partenaires/search?keyword={} - Recherche entreprises", keyword);
		List<EntreprisePartenaire> entreprises = service.searchByKeyword(keyword);
		return ResponseEntity.ok(entreprises);
	}

	@GetMapping("/count")
	public ResponseEntity<Long> countEntreprises() {
		log.info("GET /entreprises-partenaires/count - Comptage entreprises");
		long count = service.count();
		return ResponseEntity.ok(count);
	}

	@PostMapping
	public ResponseEntity<EntreprisePartenaire> createEntreprise(@Valid @RequestBody EntreprisePartenaire entreprise) {
		log.info("POST /entreprises-partenaires - Création nouvelle entreprise: {}", entreprise.getNom());
		EntreprisePartenaire savedEntreprise = service.save(entreprise);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedEntreprise);
	}

	@PutMapping("/{id}")
	public ResponseEntity<EntreprisePartenaire> updateEntreprise(@PathVariable Long id,
	                                                             @Valid @RequestBody EntreprisePartenaire entreprise) {
		log.info("PUT /entreprises-partenaires/{} - Mise à jour entreprise", id);
		try {
			EntreprisePartenaire updatedEntreprise = service.update(id, entreprise);
			return ResponseEntity.ok(updatedEntreprise);
		} catch (RuntimeException e) {
			log.error("Erreur mise à jour entreprise ID {}: {}", id, e.getMessage());
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteEntreprise(@PathVariable Long id) {
		log.info("DELETE /entreprises-partenaires/{} - Suppression entreprise", id);
		try {
			service.deleteById(id);
			return ResponseEntity.noContent().build();
		} catch (RuntimeException e) {
			log.error("Erreur suppression entreprise ID {}: {}", id, e.getMessage());
			return ResponseEntity.notFound().build();
		}
	}
}
