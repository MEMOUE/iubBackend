package com.iubbakend.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class CorsConfig {

	@Value("${cors.allowed-origins:http://localhost:4200}")
	private String allowedOrigins;

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		// Utiliser les origines spécifiques au lieu de "*"
		configuration.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));

		// Méthodes HTTP autorisées
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

		// Headers autorisés
		configuration.setAllowedHeaders(Arrays.asList("*"));

		// Autoriser les credentials
		configuration.setAllowCredentials(true);

		// Durée de cache pour les requêtes préliminaires
		configuration.setMaxAge(3600L);

		// Headers exposés
		configuration.setExposedHeaders(Arrays.asList(
				"Authorization",
				"Content-Type",
				"X-Total-Count"
		));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);

		return source;
	}
}