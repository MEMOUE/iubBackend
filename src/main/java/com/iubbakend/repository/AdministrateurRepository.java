package com.iubbakend.repository;

import com.iubbakend.entity.Administrateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdministrateurRepository extends JpaRepository<Administrateur, Long> {

	Optional<Administrateur> findByUsername(String username);

	Optional<Administrateur> findByEmail(String email);

	List<Administrateur> findByActifTrue();

	boolean existsByUsername(String username);

	boolean existsByEmail(String email);

	long countByActifTrue();
}