package com.iubbakend.service;

import com.iubbakend.entity.EcolePartenaire;
import com.iubbakend.repository.EcolePartenaireRepository;
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
public class EcolePartenaireService {
	private final EcolePartenaireRepository repository;

	public List<EcolePartenaire> findAll() {
		log.debug("Récupération de toutes les écoles partenaires");
		return repository.findByActifTrue();
	}

	public Optional<EcolePartenaire> findById(Long id) {
		log.debug("Recherche école partenaire avec ID: {}", id);
		return repository.findById(id)
				.filter(ecole -> ecole.getActif());
	}

	public List<EcolePartenaire> findByRegion(String region) {
		log.debug("Recherche écoles partenaires par région: {}", region);
		return repository.findByRegionAndActifTrue(region);
	}

	public List<EcolePartenaire> searchByKeyword(String keyword) {
		log.debug("Recherche écoles partenaires par mot-clé: {}", keyword);
		return repository.searchByKeyword(keyword);
	}

	public EcolePartenaire save(EcolePartenaire ecole) {
		log.debug("Sauvegarde école partenaire: {}", ecole.getNom());
		return repository.save(ecole);
	}

	public EcolePartenaire update(Long id, EcolePartenaire ecoleDetails) {
		log.debug("Mise à jour école partenaire ID: {}", id);

		return repository.findById(id)
				.map(existingEcole -> {
					existingEcole.setNom(ecoleDetails.getNom());
					existingEcole.setPays(ecoleDetails.getPays());
					existingEcole.setVille(ecoleDetails.getVille());
					existingEcole.setType(ecoleDetails.getType());
					existingEcole.setRegion(ecoleDetails.getRegion());
					existingEcole.setDescription(ecoleDetails.getDescription());
					existingEcole.setDomaines(ecoleDetails.getDomaines());
					existingEcole.setProgrammes(ecoleDetails.getProgrammes());
					existingEcole.setAvantages(ecoleDetails.getAvantages());
					existingEcole.setDureePartenariat(ecoleDetails.getDureePartenariat());
					existingEcole.setSiteWeb(ecoleDetails.getSiteWeb());
					existingEcole.setEmailContact(ecoleDetails.getEmailContact());

					return repository.save(existingEcole);
				})
				.orElseThrow(() -> new RuntimeException("École partenaire non trouvée avec l'ID: " + id));
	}

	public void deleteById(Long id) {
		log.debug("Suppression logique école partenaire ID: {}", id);
		repository.findById(id)
				.ifPresentOrElse(
						ecole -> {
							ecole.setActif(false);
							repository.save(ecole);
						},
						() -> {
							throw new RuntimeException("École partenaire non trouvée avec l'ID: " + id);
						}
				);
	}

	public long count() {
		return repository.countByActifTrue();
	}
}
