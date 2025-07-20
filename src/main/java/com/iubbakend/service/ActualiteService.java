package com.iubbakend.service;

import com.iubbakend.entity.Actualite;
import com.iubbakend.repository.ActualiteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ActualiteService {
	private final ActualiteRepository repository;

	public List<Actualite> findAll() {
		log.debug("Récupération de toutes les actualités publiées");
		return repository.findByActifTrueAndPublieTrueOrderByDatePublicationDesc();
	}

	public Optional<Actualite> findById(Long id) {
		log.debug("Recherche actualité avec ID: {}", id);
		Optional<Actualite> actualite = repository.findById(id)
				.filter(a -> a.getActif() && a.getPublie());

		// Incrementer le nombre de vues
		actualite.ifPresent(a -> {
			a.setNombreVues(a.getNombreVues() + 1);
			repository.save(a);
		});

		return actualite;
	}

	public List<Actualite> findByCategorie(String categorie) {
		log.debug("Recherche actualités par catégorie: {}", categorie);
		return repository.findByCategorieAndActifTrueAndPublieTrue(categorie);
	}

	public List<Actualite> findByDateRange(LocalDate startDate, LocalDate endDate) {
		log.debug("Recherche actualités entre {} et {}", startDate, endDate);
		return repository.findByDateRange(startDate, endDate);
	}

	public List<Actualite> searchByKeyword(String keyword) {
		log.debug("Recherche actualités par mot-clé: {}", keyword);
		return repository.searchByKeyword(keyword);
	}

	public Actualite save(Actualite actualite) {
		log.debug("Sauvegarde actualité: {}", actualite.getTitre());

		if (actualite.getDatePublication() == null) {
			actualite.setDatePublication(LocalDate.now());
		}

		return repository.save(actualite);
	}

	public Actualite update(Long id, Actualite actualiteDetails) {
		log.debug("Mise à jour actualité ID: {}", id);

		return repository.findById(id)
				.map(existingActualite -> {
					existingActualite.setTitre(actualiteDetails.getTitre());
					existingActualite.setDescription(actualiteDetails.getDescription());
					existingActualite.setContenu(actualiteDetails.getContenu());
					existingActualite.setImageUrl(actualiteDetails.getImageUrl());
					existingActualite.setDatePublication(actualiteDetails.getDatePublication());
					existingActualite.setDateEvenement(actualiteDetails.getDateEvenement());
					existingActualite.setCategorie(actualiteDetails.getCategorie());
					existingActualite.setAuteur(actualiteDetails.getAuteur());
					existingActualite.setPublie(actualiteDetails.getPublie());

					return repository.save(existingActualite);
				})
				.orElseThrow(() -> new RuntimeException("Actualité non trouvée avec l'ID: " + id));
	}

	public Actualite publier(Long id) {
		log.debug("Publication actualité ID: {}", id);

		return repository.findById(id)
				.map(actualite -> {
					actualite.setPublie(true);
					if (actualite.getDatePublication() == null) {
						actualite.setDatePublication(LocalDate.now());
					}
					return repository.save(actualite);
				})
				.orElseThrow(() -> new RuntimeException("Actualité non trouvée avec l'ID: " + id));
	}

	public void deleteById(Long id) {
		log.debug("Suppression logique actualité ID: {}", id);
		repository.findById(id)
				.ifPresentOrElse(
						actualite -> {
							actualite.setActif(false);
							repository.save(actualite);
						},
						() -> {
							throw new RuntimeException("Actualité non trouvée avec l'ID: " + id);
						}
				);
	}

	public long count() {
		return repository.countByActifTrueAndPublieTrue();
	}
}
