package com.iubbakend.repository;

import com.iubbakend.entity.EntreprisePartenaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface EntreprisePartenaireRepository extends JpaRepository<EntreprisePartenaire, Long> {
	List<EntreprisePartenaire> findByActifTrue();

	List<EntreprisePartenaire> findBySecteurAndActifTrue(String secteur);

	@Query("SELECT e FROM EntreprisePartenaire e WHERE e.actif = true AND " +
			"(LOWER(e.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
			"LOWER(e.secteur) LIKE LOWER(CONCAT('%', :keyword, '%')))")
	List<EntreprisePartenaire> searchByKeyword(@Param("keyword") String keyword);

	long countByActifTrue();
}
