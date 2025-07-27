package com.iubbakend.controller;

import com.iubbakend.dto.LoginRequest;
import com.iubbakend.dto.LoginResponse;
import com.iubbakend.entity.Administrateur;
import com.iubbakend.service.AdministrateurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/administrateurs")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${cors.allowed-origins}")
@Tag(name = "Administration", description = "API de gestion des administrateurs")
public class AdministrateurController {

	private final AdministrateurService service;

	@GetMapping
	@Operation(summary = "Lister tous les administrateurs", description = "Récupère la liste de tous les administrateurs actifs")
	public ResponseEntity<List<Administrateur>> getAllAdministrateurs() {
		log.info("GET /administrateurs - Récupération de tous les administrateurs");
		List<Administrateur> admins = service.findAll();
		// Masquer les mots de passe dans la réponse
		admins.forEach(admin -> admin.setPassword("****"));
		return ResponseEntity.ok(admins);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Obtenir un administrateur par ID", description = "Récupère un administrateur spécifique par son ID")
	public ResponseEntity<Administrateur> getAdministrateurById(@PathVariable Long id) {
		log.info("GET /administrateurs/{} - Récupération administrateur par ID", id);
		return service.findById(id)
				.map(admin -> {
					admin.setPassword("****"); // Masquer le mot de passe
					return ResponseEntity.ok(admin);
				})
				.orElse(ResponseEntity.notFound().build());
	}

	@GetMapping("/email/{email}")
	@Operation(summary = "Obtenir un administrateur par email", description = "Récupère un administrateur par son adresse email")
	public ResponseEntity<Administrateur> getAdministrateurByEmail(@PathVariable String email) {
		log.info("GET /administrateurs/email/{} - Récupération administrateur par email", email);
		return service.findByEmail(email)
				.map(admin -> {
					admin.setPassword("****"); // Masquer le mot de passe
					return ResponseEntity.ok(admin);
				})
				.orElse(ResponseEntity.notFound().build());
	}

	@GetMapping("/username/{username}")
	@Operation(summary = "Obtenir un administrateur par username", description = "Récupère un administrateur par son nom d'utilisateur")
	public ResponseEntity<Administrateur> getAdministrateurByUsername(@PathVariable String username) {
		log.info("GET /administrateurs/username/{} - Récupération administrateur par username", username);
		return service.findByUsername(username)
				.map(admin -> {
					admin.setPassword("****"); // Masquer le mot de passe
					return ResponseEntity.ok(admin);
				})
				.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping("/login")
	@Operation(
			summary = "Connexion administrateur",
			description = "Authentifie un administrateur avec son email et mot de passe",
			responses = {
					@ApiResponse(responseCode = "200", description = "Connexion réussie"),
					@ApiResponse(responseCode = "401", description = "Identifiants incorrects")
			}
	)
	public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
		log.info("POST /administrateurs/login - Tentative de connexion pour: {}",
				loginRequest.getEmail());

		if (service.verifierMotDePasse(loginRequest.getEmail(), loginRequest.getPassword())) {
			return service.findByEmail(loginRequest.getEmail())
					.map(admin -> {
						LoginResponse.AdministrateurInfo adminInfo = new LoginResponse.AdministrateurInfo(
								admin.getId(),
								admin.getUsername(),
								admin.getNom(),
								admin.getPrenom(),
								admin.getEmail(),
								admin.getRole(),
								admin.getPremiereConnexion()
						);

						LoginResponse response = new LoginResponse(
								true,
								"Connexion réussie",
								adminInfo
						);

						log.info("Connexion réussie pour: {}", loginRequest.getEmail());
						return ResponseEntity.ok(response);
					})
					.orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
							.body(new LoginResponse(false, "Utilisateur introuvable", null)));
		} else {
			log.warn("Échec de connexion pour: {}", loginRequest.getEmail());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(new LoginResponse(false, "Identifiants incorrects", null));
		}
	}

	@PostMapping
	@Operation(summary = "Créer un administrateur", description = "Crée un nouvel administrateur")
	public ResponseEntity<Administrateur> createAdministrateur(@Valid @RequestBody Administrateur administrateur) {
		log.info("POST /administrateurs - Création nouvel administrateur: {}", administrateur.getUsername());

		try {
			Administrateur savedAdmin = service.save(administrateur);
			savedAdmin.setPassword("****"); // Masquer le mot de passe dans la réponse
			return ResponseEntity.status(HttpStatus.CREATED).body(savedAdmin);
		} catch (RuntimeException e) {
			log.error("Erreur création administrateur: {}", e.getMessage());
			return ResponseEntity.badRequest().build();
		}
	}

	@PutMapping("/{id}")
	@Operation(summary = "Mettre à jour un administrateur", description = "Met à jour les informations d'un administrateur")
	public ResponseEntity<Administrateur> updateAdministrateur(@PathVariable Long id,
	                                                           @Valid @RequestBody Administrateur administrateur) {
		log.info("PUT /administrateurs/{} - Mise à jour administrateur", id);
		try {
			Administrateur updatedAdmin = service.update(id, administrateur);
			updatedAdmin.setPassword("****"); // Masquer le mot de passe
			return ResponseEntity.ok(updatedAdmin);
		} catch (RuntimeException e) {
			log.error("Erreur mise à jour administrateur ID {}: {}", id, e.getMessage());
			return ResponseEntity.notFound().build();
		}
	}

	@PutMapping("/{id}/changer-mot-de-passe")
	@Operation(
			summary = "Changer le mot de passe",
			description = "Change le mot de passe d'un administrateur",
			responses = {
					@ApiResponse(responseCode = "200", description = "Mot de passe changé avec succès"),
					@ApiResponse(responseCode = "400", description = "Ancien mot de passe incorrect ou erreur de validation")
			}
	)
//	public ResponseEntity<Map<String, Object>> changerMotDePasse(@PathVariable Long id,
//	                                                             @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
//		log.info("PUT /administrateurs/{}/changer-mot-de-passe - Changement mot de passe", id);
//
//		try {
//			service.changerMotDePasse(id,
//					changePasswordRequest.getAncienMotDePasse(),
//					changePasswordRequest.getNouveauMotDePasse());
//
//			return ResponseEntity.ok(Map.of(
//					"success", true,
//					"message", "Mot de passe changé avec succès"
//			));
//		} catch (RuntimeException e) {
//			log.error("Erreur changement mot de passe ID {}: {}", id, e.getMessage());
//			return ResponseEntity.badRequest().body(Map.of(
//					"success", false,
//					"message", e.getMessage()
//			));
//		}
//	}

	@GetMapping("/count")
	public ResponseEntity<Long> countAdministrateurs() {
		log.info("GET /administrateurs/count - Comptage administrateurs");
		long count = service.count();
		return ResponseEntity.ok(count);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Supprimer un administrateur", description = "Supprime (désactive) un administrateur")
	public ResponseEntity<Void> deleteAdministrateur(@PathVariable Long id) {
		log.info("DELETE /administrateurs/{} - Suppression administrateur", id);
		try {
			service.deleteById(id);
			return ResponseEntity.noContent().build();
		} catch (RuntimeException e) {
			log.error("Erreur suppression administrateur ID {}: {}", id, e.getMessage());
			return ResponseEntity.notFound().build();
		}
	}
}