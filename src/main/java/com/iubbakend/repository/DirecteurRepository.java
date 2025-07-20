package com.iubbakend.repository;

import com.iubbakend.entity.Directeur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface DirecteurRepository extends JpaRepository<Directeur, Long> {
	Optional<Directeur> findFirstByActifTrueOrderByCreatedAtDesc();
}
