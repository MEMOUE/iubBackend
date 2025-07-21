package com.iubbakend.service;

import com.iubbakend.entity.Directeur;
import com.iubbakend.repository.DirecteurRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DirecteurService {
	private final DirecteurRepository repository;

	public List<Directeur> findAll() {
		log.debug("Récupération de toutes les informations directeur");
		return repository.findAll();
	}

	public Optional<Directeur> findById(Long id) {
		log.debug("Recherche directeur avec ID: {}", id);
		return repository.findById(id);
	}

	public Optional<Directeur> findCurrentDirecteur() {
		log.debug("Recherche du directeur actuel");
		return repository.findFirstByActifTrueOrderByCreatedAtDesc();
	}

	public Directeur save(Directeur directeur) {
		log.debug("Sauvegarde informations directeur: {}", directeur.getNom());

		// Désactiver tous les autres directeurs avant d'enregistrer le nouveau
		if (directeur.getActif() == null || directeur.getActif()) {
			desactiverTousLesDirecteurs();
			directeur.setActif(true);
		}

		return repository.save(directeur);
	}

	public Directeur update(Long id, Directeur directeurDetails) {
		log.debug("Mise à jour directeur ID: {}", id);

		return repository.findById(id)
				.map(existingDirecteur -> {
					// Si on active ce directeur, désactiver tous les autres
					if (directeurDetails.getActif() != null && directeurDetails.getActif() && !existingDirecteur.getActif()) {
						desactiverTousLesDirecteurs();
					}

					existingDirecteur.setNom(directeurDetails.getNom());
					existingDirecteur.setTitre(directeurDetails.getTitre());
					existingDirecteur.setPhotoUrl(directeurDetails.getPhotoUrl());
					existingDirecteur.setExperience(directeurDetails.getExperience());
					existingDirecteur.setDiplomes(directeurDetails.getDiplomes());
					existingDirecteur.setMessageBienvenue(directeurDetails.getMessageBienvenue());
					existingDirecteur.setMessageVision(directeurDetails.getMessageVision());
					existingDirecteur.setMessageEngagements(directeurDetails.getMessageEngagements());
					existingDirecteur.setMessageEtudiants(directeurDetails.getMessageEtudiants());
					existingDirecteur.setEmail(directeurDetails.getEmail());
					existingDirecteur.setTelephone(directeurDetails.getTelephone());
					existingDirecteur.setAdresse(directeurDetails.getAdresse());
					existingDirecteur.setLinkedinUrl(directeurDetails.getLinkedinUrl());
					existingDirecteur.setActif(directeurDetails.getActif() != null ? directeurDetails.getActif() : true);

					return repository.save(existingDirecteur);
				})
				.orElseThrow(() -> new RuntimeException("Directeur non trouvé avec l'ID: " + id));
	}

	public Directeur activerDirecteur(Long id) {
		log.debug("Activation du directeur ID: {}", id);

		return repository.findById(id)
				.map(directeur -> {
					// Désactiver tous les autres directeurs
					desactiverTousLesDirecteurs();

					// Activer le directeur sélectionné
					directeur.setActif(true);
					return repository.save(directeur);
				})
				.orElseThrow(() -> new RuntimeException("Directeur non trouvé avec l'ID: " + id));
	}

	public void deleteById(Long id) {
		log.debug("Suppression logique directeur ID: {}", id);
		repository.findById(id)
				.ifPresentOrElse(
						directeur -> {
							directeur.setActif(false);
							repository.save(directeur);
						},
						() -> {
							throw new RuntimeException("Directeur non trouvé avec l'ID: " + id);
						}
				);
	}

	/**
	 * Désactive tous les directeurs actuellement actifs
	 */
	private void desactiverTousLesDirecteurs() {
		log.debug("Désactivation de tous les directeurs actifs");
		List<Directeur> directeursActifs = repository.findAll().stream()
				.filter(Directeur::getActif)
				.toList();

		directeursActifs.forEach(directeur -> {
			directeur.setActif(false);
			repository.save(directeur);
		});

		log.debug("{} directeur(s) désactivé(s)", directeursActifs.size());
	}

	/**
	 * Vérifie s'il existe déjà un directeur actif
	 */
	public boolean existeDirecteurActif() {
		return repository.findFirstByActifTrueOrderByCreatedAtDesc().isPresent();
	}

	/**
	 * Compte le nombre total de directeurs (actifs et inactifs)
	 */
	public long countAll() {
		return repository.count();
	}

	/**
	 * Compte le nombre de directeurs actifs (devrait toujours être 0 ou 1)
	 */
	public long countActifs() {
		return repository.findAll().stream()
				.filter(Directeur::getActif)
				.count();
	}
}