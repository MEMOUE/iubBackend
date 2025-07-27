package com.iubbakend.service;

import com.iubbakend.entity.Administrateur;
import com.iubbakend.repository.AdministrateurRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AdministrateurService {

	private final AdministrateurRepository repository;
	private final BCryptPasswordEncoder passwordEncoder;

	public List<Administrateur> findAll() {
		log.debug("Récupération de tous les administrateurs actifs");
		return repository.findByActifTrue();
	}

	public Optional<Administrateur> findById(Long id) {
		log.debug("Recherche administrateur avec ID: {}", id);
		return repository.findById(id)
				.filter(Administrateur::getActif);
	}

	public Optional<Administrateur> findByUsername(String username) {
		log.debug("Recherche administrateur par username: {}", username);
		return repository.findByUsername(username)
				.filter(Administrateur::getActif);
	}

	public Administrateur save(Administrateur administrateur) {
		log.debug("Sauvegarde administrateur: {}", administrateur.getUsername());

		// Vérifier l'unicité du username et email
		if (repository.existsByUsername(administrateur.getUsername())) {
			throw new RuntimeException("Ce nom d'utilisateur existe déjà");
		}

		if (repository.existsByEmail(administrateur.getEmail())) {
			throw new RuntimeException("Cet email existe déjà");
		}

		// Encoder le mot de passe
		administrateur.setPassword(passwordEncoder.encode(administrateur.getPassword()));

		return repository.save(administrateur);
	}

	public Administrateur update(Long id, Administrateur adminDetails) {
		log.debug("Mise à jour administrateur ID: {}", id);

		return repository.findById(id)
				.map(existingAdmin -> {
					existingAdmin.setNom(adminDetails.getNom());
					existingAdmin.setPrenom(adminDetails.getPrenom());
					existingAdmin.setEmail(adminDetails.getEmail());
					existingAdmin.setTelephone(adminDetails.getTelephone());
					existingAdmin.setRole(adminDetails.getRole());

					// Ne pas modifier le username et password ici
					return repository.save(existingAdmin);
				})
				.orElseThrow(() -> new RuntimeException("Administrateur non trouvé avec l'ID: " + id));
	}

	public Administrateur changerMotDePasse(Long id, String ancienMotDePasse, String nouveauMotDePasse) {
		log.debug("Changement de mot de passe pour administrateur ID: {}", id);

		return repository.findById(id)
				.map(admin -> {
					// Vérifier l'ancien mot de passe
					if (!passwordEncoder.matches(ancienMotDePasse, admin.getPassword())) {
						throw new RuntimeException("Ancien mot de passe incorrect");
					}

					admin.setPassword(passwordEncoder.encode(nouveauMotDePasse));
					admin.setPremiereConnexion(false);

					return repository.save(admin);
				})
				.orElseThrow(() -> new RuntimeException("Administrateur non trouvé avec l'ID: " + id));
	}

	public boolean verifierMotDePasse(String email, String motDePasse) {
		Optional<Administrateur> admin = findByEmail(email);

		if (admin.isPresent()) {
			return passwordEncoder.matches(motDePasse, admin.get().getPassword());
		}

		return false;
	}

	public Optional<Administrateur> findByEmail(String email) {
		log.debug("Recherche administrateur par email: {}", email);
		return repository.findByEmail(email)
				.filter(Administrateur::getActif);
	}

	public void deleteById(Long id) {
		log.debug("Suppression logique administrateur ID: {}", id);
		repository.findById(id)
				.ifPresentOrElse(
						admin -> {
							admin.setActif(false);
							repository.save(admin);
						},
						() -> {
							throw new RuntimeException("Administrateur non trouvé avec l'ID: " + id);
						}
				);
	}

	public long count() {
		return repository.countByActifTrue();
	}

	/**
	 * Crée l'administrateur par défaut si aucun n'existe
	 */
	public void creerAdministrateurParDefaut() {
		if (repository.countByActifTrue() == 0) {
			log.info("Aucun administrateur trouvé. Création de l'administrateur par défaut...");

			Administrateur adminDefaut = new Administrateur();
			adminDefaut.setUsername("admin");
			adminDefaut.setPassword("admin123"); // Sera encodé automatiquement
			adminDefaut.setNom("Administrateur");
			adminDefaut.setPrenom("Principal");
			adminDefaut.setEmail("admin@iub-university.com");
			adminDefaut.setRole(Administrateur.Role.SUPER_ADMIN);
			adminDefaut.setActif(true);
			adminDefaut.setPremiereConnexion(true);

			save(adminDefaut);

			log.info("Administrateur par défaut créé avec succès !");
			log.info("Username: admin");
			log.info("Password: admin123");
			log.info("IMPORTANT: Changez le mot de passe lors de la première connexion !");
		} else {
			log.info("Administrateur(s) déjà existant(s). Aucune création nécessaire.");
		}
	}
}