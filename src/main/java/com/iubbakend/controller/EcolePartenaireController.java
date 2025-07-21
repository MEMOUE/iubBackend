package com.iubbakend.controller;

import com.iubbakend.entity.EcolePartenaire;
import com.iubbakend.service.EcolePartenaireService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/ecoles-partenaires")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${cors.allowed-origins}")
public class EcolePartenaireController {
	private final EcolePartenaireService service;

	@GetMapping
	public ResponseEntity<List<EcolePartenaire>> getAllEcoles() {
		log.info("GET /ecoles-partenaires - Récupération de toutes les écoles");
		List<EcolePartenaire> ecoles = service.findAll();
		return ResponseEntity.ok(ecoles);
	}

	@GetMapping("/{id}")
	public ResponseEntity<EcolePartenaire> getEcoleById(@PathVariable Long id) {
		log.info("GET /ecoles-partenaires/{} - Récupération école par ID", id);
		return service.findById(id)
				.map(ecole -> ResponseEntity.ok(ecole))
				.orElse(ResponseEntity.notFound().build());
	}

	@GetMapping("/region/{region}")
	public ResponseEntity<List<EcolePartenaire>> getEcolesByRegion(@PathVariable String region) {
		log.info("GET /ecoles-partenaires/region/{} - Récupération écoles par région", region);
		List<EcolePartenaire> ecoles = service.findByRegion(region);
		return ResponseEntity.ok(ecoles);
	}

	@GetMapping("/search")
	public ResponseEntity<List<EcolePartenaire>> searchEcoles(@RequestParam String keyword) {
		log.info("GET /ecoles-partenaires/search?keyword={} - Recherche écoles", keyword);
		List<EcolePartenaire> ecoles = service.searchByKeyword(keyword);
		return ResponseEntity.ok(ecoles);
	}

	@GetMapping("/count")
	public ResponseEntity<Long> countEcoles() {
		log.info("GET /ecoles-partenaires/count - Comptage écoles");
		long count = service.count();
		return ResponseEntity.ok(count);
	}

	@PostMapping
	public ResponseEntity<EcolePartenaire> createEcole(@Valid @RequestBody EcolePartenaire ecole) {
		log.info("POST /ecoles-partenaires - Création nouvelle école: {}", ecole.getNom());
		EcolePartenaire savedEcole = service.save(ecole);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedEcole);
	}

	@PutMapping("/{id}")
	public ResponseEntity<EcolePartenaire> updateEcole(@PathVariable Long id,
	                                                   @Valid @RequestBody EcolePartenaire ecole) {
		log.info("PUT /ecoles-partenaires/{} - Mise à jour école", id);
		try {
			EcolePartenaire updatedEcole = service.update(id, ecole);
			return ResponseEntity.ok(updatedEcole);
		} catch (RuntimeException e) {
			log.error("Erreur mise à jour école ID {}: {}", id, e.getMessage());
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteEcole(@PathVariable Long id) {
		log.info("DELETE /ecoles-partenaires/{} - Suppression école", id);
		try {
			service.deleteById(id);
			return ResponseEntity.noContent().build();
		} catch (RuntimeException e) {
			log.error("Erreur suppression école ID {}: {}", id, e.getMessage());
			return ResponseEntity.notFound().build();
		}
	}
}
