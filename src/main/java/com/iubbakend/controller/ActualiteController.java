package com.iubbakend.controller;

import com.iubbakend.entity.Actualite;
import com.iubbakend.service.ActualiteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
@RestController
@RequestMapping("/api/actualites")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${cors.allowed-origins}")
public class ActualiteController {
	private final ActualiteService service;

	@GetMapping
	public ResponseEntity<List<Actualite>> getAllActualites() {
		log.info("GET /actualites - Récupération de toutes les actualités");
		List<Actualite> actualites = service.findAll();
		return ResponseEntity.ok(actualites);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Actualite> getActualiteById(@PathVariable Long id) {
		log.info("GET /actualites/{} - Récupération actualité par ID", id);
		return service.findById(id)
				.map(actualite -> ResponseEntity.ok(actualite))
				.orElse(ResponseEntity.notFound().build());
	}

	@GetMapping("/categorie/{categorie}")
	public ResponseEntity<List<Actualite>> getActualitesByCategorie(@PathVariable String categorie) {
		log.info("GET /actualites/categorie/{} - Récupération actualités par catégorie", categorie);
		List<Actualite> actualites = service.findByCategorie(categorie);
		return ResponseEntity.ok(actualites);
	}

	@GetMapping("/date-range")
	public ResponseEntity<List<Actualite>> getActualitesByDateRange(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
		log.info("GET /actualites/date-range - Récupération actualités entre {} et {}", startDate, endDate);
		List<Actualite> actualites = service.findByDateRange(startDate, endDate);
		return ResponseEntity.ok(actualites);
	}

	@GetMapping("/search")
	public ResponseEntity<List<Actualite>> searchActualites(@RequestParam String keyword) {
		log.info("GET /actualites/search?keyword={} - Recherche actualités", keyword);
		List<Actualite> actualites = service.searchByKeyword(keyword);
		return ResponseEntity.ok(actualites);
	}

	@GetMapping("/count")
	public ResponseEntity<Long> countActualites() {
		log.info("GET /actualites/count - Comptage actualités");
		long count = service.count();
		return ResponseEntity.ok(count);
	}

	@PostMapping
	public ResponseEntity<Actualite> createActualite(@Valid @RequestBody Actualite actualite) {
		log.info("POST /actualites - Création nouvelle actualité: {}", actualite.getTitre());
		Actualite savedActualite = service.save(actualite);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedActualite);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Actualite> updateActualite(@PathVariable Long id,
	                                                 @Valid @RequestBody Actualite actualite) {
		log.info("PUT /actualites/{} - Mise à jour actualité", id);
		try {
			Actualite updatedActualite = service.update(id, actualite);
			return ResponseEntity.ok(updatedActualite);
		} catch (RuntimeException e) {
			log.error("Erreur mise à jour actualité ID {}: {}", id, e.getMessage());
			return ResponseEntity.notFound().build();
		}
	}

	@PutMapping("/{id}/publier")
	public ResponseEntity<Actualite> publierActualite(@PathVariable Long id) {
		log.info("PUT /actualites/{}/publier - Publication actualité", id);
		try {
			Actualite actualite = service.publier(id);
			return ResponseEntity.ok(actualite);
		} catch (RuntimeException e) {
			log.error("Erreur publication actualité ID {}: {}", id, e.getMessage());
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteActualite(@PathVariable Long id) {
		log.info("DELETE /actualites/{} - Suppression actualité", id);
		try {
			service.deleteById(id);
			return ResponseEntity.noContent().build();
		} catch (RuntimeException e) {
			log.error("Erreur suppression actualité ID {}: {}", id, e.getMessage());
			return ResponseEntity.notFound().build();
		}
	}
}
