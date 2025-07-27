package com.iubbakend.conf;

import com.iubbakend.service.AdministrateurService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

	private final AdministrateurService administrateurService;

	@Override
	public void run(String... args) throws Exception {
		log.info("=== INITIALISATION DE L'APPLICATION IUB ===");

		try {
			// Créer l'administrateur par défaut si nécessaire
			administrateurService.creerAdministrateurParDefaut();

			log.info("=== INITIALISATION TERMINÉE AVEC SUCCÈS ===");

		} catch (Exception e) {
			log.error("Erreur lors de l'initialisation de l'application: {}", e.getMessage(), e);
		}
	}
}