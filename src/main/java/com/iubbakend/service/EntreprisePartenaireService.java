package com.iubbakend.service;

import com.iubbakend.entity.EntreprisePartenaire;
import com.iubbakend.repository.EntreprisePartenaireRepository;
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
public class EntreprisePartenaireService {
	private final EntreprisePartenaireRepository repository;

	public List<EntreprisePartenaire> findAll() {
		log.debug("Récupération de toutes les entreprises partenaires");
		return repository.findByActifTrue();
	}

	public Optional<EntreprisePartenaire> findById(Long id) {
		log.debug("Recherche entreprise partenaire avec ID: {}", id);
		return repository.findById(id)
				.filter(entreprise -> entreprise.getActif());
	}

	public List<EntreprisePartenaire> findBySecteur(String secteur) {
		log.debug("Recherche entreprises partenaires par secteur: {}", secteur);
		return repository.findBySecteurAndActifTrue(secteur);
	}

	public List<EntreprisePartenaire> searchByKeyword(String keyword) {
		log.debug("Recherche entreprises partenaires par mot-clé: {}", keyword);
		return repository.searchByKeyword(keyword);
	}

	public EntreprisePartenaire save(EntreprisePartenaire entreprise) {
		log.debug("Sauvegarde entreprise partenaire: {}", entreprise.getNom());
		return repository.save(entreprise);
	}

	public EntreprisePartenaire update(Long id, EntreprisePartenaire entrepriseDetails) {
		log.debug("Mise à jour entreprise partenaire ID: {}", id);

		return repository.findById(id)
				.map(existingEntreprise -> {
					existingEntreprise.setNom(entrepriseDetails.getNom());
					existingEntreprise.setSecteur(entrepriseDetails.getSecteur());
					existingEntreprise.setTaille(entrepriseDetails.getTaille());
					existingEntreprise.setLocalisation(entrepriseDetails.getLocalisation());
					existingEntreprise.setTypePartenariat(entrepriseDetails.getTypePartenariat());
					existingEntreprise.setDescription(entrepriseDetails.getDescription());
					existingEntreprise.setCollaborations(entrepriseDetails.getCollaborations());
					existingEntreprise.setPostes(entrepriseDetails.getPostes());
					existingEntreprise.setAvantages(entrepriseDetails.getAvantages());
					existingEntreprise.setDureePartenariat(entrepriseDetails.getDureePartenariat());
					existingEntreprise.setSiteWeb(entrepriseDetails.getSiteWeb());
					existingEntreprise.setEmailContact(entrepriseDetails.getEmailContact());
					existingEntreprise.setResponsableContact(entrepriseDetails.getResponsableContact());

					return repository.save(existingEntreprise);
				})
				.orElseThrow(() -> new RuntimeException("Entreprise partenaire non trouvée avec l'ID: " + id));
	}

	public void deleteById(Long id) {
		log.debug("Suppression logique entreprise partenaire ID: {}", id);
		repository.findById(id)
				.ifPresentOrElse(
						entreprise -> {
							entreprise.setActif(false);
							repository.save(entreprise);
						},
						() -> {
							throw new RuntimeException("Entreprise partenaire non trouvée avec l'ID: " + id);
						}
				);
	}

	public long count() {
		return repository.countByActifTrue();
	}
}
