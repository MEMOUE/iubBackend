package com.iubbakend.repository;

import com.iubbakend.entity.EcolePartenaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface EcolePartenaireRepository extends JpaRepository<EcolePartenaire, Long> {
	List<EcolePartenaire> findByActifTrue();

	List<EcolePartenaire> findByRegionAndActifTrue(String region);

	@Query("SELECT e FROM EcolePartenaire e WHERE e.actif = true AND " +
			"(LOWER(e.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
			"LOWER(e.pays) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
			"LOWER(e.ville) LIKE LOWER(CONCAT('%', :keyword, '%')))")
	List<EcolePartenaire> searchByKeyword(@Param("keyword") String keyword);

	long countByActifTrue();
}
