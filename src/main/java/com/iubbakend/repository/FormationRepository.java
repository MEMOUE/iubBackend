package com.iubbakend.repository;

import com.iubbakend.entity.Formation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface FormationRepository extends JpaRepository<Formation, Long> {
	List<Formation> findByActifTrueAndDisponibleTrue();

	List<Formation> findByCategorieAndActifTrueAndDisponibleTrue(String categorie);

	@Query("SELECT f FROM Formation f WHERE f.actif = true AND f.disponible = true AND " +
			"(LOWER(f.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
			"LOWER(f.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
	List<Formation> searchByKeyword(@Param("keyword") String keyword);

	long countByActifTrueAndDisponibleTrue();
}
