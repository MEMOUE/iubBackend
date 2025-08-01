package com.iubbakend.conf;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class OpenApiConfig {
	@Value("${server.port:8080}")
	private String serverPort;

	@Bean
	public OpenAPI iubOpenAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("IUB - International University of Bouake API")
						.description("""
                            API REST complète pour la gestion du site web de l'International University of Bouake.
                            
                            Cette API permet de gérer :
                            - 🏫 Les écoles partenaires internationales
                            - 🏢 Les entreprises partenaires
                            - 👨‍💼 Les informations du directeur
                            - 📰 Les actualités et événements
                            - 🎓 Les formations proposées
                            
                            **Fonctionnalités principales :**
                            - CRUD complet pour toutes les entités
                            - Recherche avancée par mots-clés
                            - Filtrage par catégories/régions/secteurs
                            - Gestion de la publication des actualités
                            - Système d'inscription aux formations
                            - Compteurs et statistiques
                            """)
						.version("1.0.0")
						.contact(new Contact()
								.name("Équipe de développement IUB")
								.email("dev@iub-university.com")
								.url("https://www.iub-university.com"))
						.license(new License()
								.name("MIT License")
								.url("https://opensource.org/licenses/MIT")))
				.servers(List.of(
						new Server()
								.url("http://localhost:" + serverPort)
								.description("Serveur de développement local"),
						new Server()
								.url("https://api.iub-university.com")
								.description("Serveur de production")
				));
	}
}