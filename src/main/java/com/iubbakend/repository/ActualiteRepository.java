package com.iubbakend.repository;

import com.iubbakend.entity.Actualite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
@Repository
public interface ActualiteRepository extends JpaRepository<Actualite, Long> {
	List<Actualite> findByActifTrueAndPublieTrueOrderByDatePublicationDesc();

	List<Actualite> findByCategorieAndActifTrueAndPublieTrue(String categorie);

	@Query("SELECT a FROM Actualite a WHERE a.actif = true AND a.publie = true AND " +
			"a.datePublication BETWEEN :startDate AND :endDate ORDER BY a.datePublication DESC")
	List<Actualite> findByDateRange(@Param("startDate") LocalDate startDate,
	                                @Param("endDate") LocalDate endDate);

	@Query("SELECT a FROM Actualite a WHERE a.actif = true AND a.publie = true AND " +
			"(LOWER(a.titre) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
			"LOWER(a.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
	List<Actualite> searchByKeyword(@Param("keyword") String keyword);

	long countByActifTrueAndPublieTrue();
}
