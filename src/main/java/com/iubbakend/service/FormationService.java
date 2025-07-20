package com.iubbakend.service;

import com.iubbakend.entity.Formation;
import com.iubbakend.repository.FormationRepository;
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
public class FormationService {
	private final FormationRepository repository;

	public List<Formation> findAll() {
		log.debug("Récupération de toutes les formations disponibles");
		return repository.findByActifTrueAndDisponibleTrue();
	}

	public Optional<Formation> findById(Long id) {
		log.debug("Recherche formation avec ID: {}", id);
		return repository.findById(id)
				.filter(formation -> formation.getActif() && formation.getDisponible());
	}

	public List<Formation> findByCategorie(String categorie) {
		log.debug("Recherche formations par catégorie: {}", categorie);
		return repository.findByCategorieAndActifTrueAndDisponibleTrue(categorie);
	}

	public List<Formation> searchByKeyword(String keyword) {
		log.debug("Recherche formations par mot-clé: {}", keyword);
		return repository.searchByKeyword(keyword);
	}

	public Formation save(Formation formation) {
		log.debug("Sauvegarde formation: {}", formation.getNom());
		return repository.save(formation);
	}

	public Formation update(Long id, Formation formationDetails) {
		log.debug("Mise à jour formation ID: {}", id);

		return repository.findById(id)
				.map(existingFormation -> {
					existingFormation.setNom(formationDetails.getNom());
					existingFormation.setDiplome(formationDetails.getDiplome());
					existingFormation.setDuree(formationDetails.getDuree());
					existingFormation.setCategorie(formationDetails.getCategorie());
					existingFormation.setDescription(formationDetails.getDescription());
					existingFormation.setObjectifs(formationDetails.getObjectifs());
					existingFormation.setDebouches(formationDetails.getDebouches());
					existingFormation.setFraisScolarite(formationDetails.getFraisScolarite());
					existingFormation.setConditionsAdmission(formationDetails.getConditionsAdmission());
					existingFormation.setProgrammeDetaille(formationDetails.getProgrammeDetaille());
					existingFormation.setNombreSemestres(formationDetails.getNombreSemestres());
					existingFormation.setNombrePlaces(formationDetails.getNombrePlaces());
					existingFormation.setDisponible(formationDetails.getDisponible());

					return repository.save(existingFormation);
				})
				.orElseThrow(() -> new RuntimeException("Formation non trouvée avec l'ID: " + id));
	}

	public Formation inscrireEtudiant(Long id) {
		log.debug("Inscription étudiant à la formation ID: {}", id);

		return repository.findById(id)
				.map(formation -> {
					if (formation.getNombreInscrits() < formation.getNombrePlaces()) {
						formation.setNombreInscrits(formation.getNombreInscrits() + 1);
						return repository.save(formation);
					} else {
						throw new RuntimeException("Formation complète - plus de places disponibles");
					}
				})
				.orElseThrow(() -> new RuntimeException("Formation non trouvée avec l'ID: " + id));
	}

	public void deleteById(Long id) {
		log.debug("Suppression logique formation ID: {}", id);
		repository.findById(id)
				.ifPresentOrElse(
						formation -> {
							formation.setActif(false);
							repository.save(formation);
						},
						() -> {
							throw new RuntimeException("Formation non trouvée avec l'ID: " + id);
						}
				);
	}

	public long count() {
		return repository.countByActifTrueAndDisponibleTrue();
	}
}
