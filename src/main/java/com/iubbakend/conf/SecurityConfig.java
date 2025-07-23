package com.iubbakend.conf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Autowired
	private CorsConfigurationSource corsConfigurationSource;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				// Configuration CORS en utilisant le bean défini dans CorsConfig
				.cors(cors -> cors.configurationSource(corsConfigurationSource))

				// Désactiver CSRF pour les APIs REST
				.csrf(csrf -> csrf.disable())

				// Configuration des sessions (stateless pour API REST)
				.sessionManagement(session ->
						session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				// Configuration des autorisations
				.authorizeHttpRequests(auth -> auth
						// Autoriser tous les endpoints de l'API publiquement
						.requestMatchers("/api/actualites/**").permitAll()
						.requestMatchers("/api/directeur/**").permitAll()
						.requestMatchers("/api/ecoles-partenaires/**").permitAll()
						.requestMatchers("/api/entreprises-partenaires/**").permitAll()
						.requestMatchers("/api/formations/**").permitAll()
						.requestMatchers("/api/upload/**").permitAll()

						// Autoriser l'accès aux fichiers uploadés
						.requestMatchers("/uploads/**").permitAll()

						// Autoriser Swagger UI et documentation
						.requestMatchers("/swagger-ui/**").permitAll()
						.requestMatchers("/swagger-ui.html").permitAll()
						.requestMatchers("/v3/api-docs/**").permitAll()
						.requestMatchers("/api-docs/**").permitAll()

						// Autoriser les ressources statiques
						.requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
						.requestMatchers("/static/**").permitAll()

						// Autoriser les requêtes OPTIONS (preflight CORS)
						.requestMatchers("OPTIONS", "/**").permitAll()

						// Toutes les autres requêtes sont autorisées pour le développement
						.anyRequest().permitAll()
				)

				// Désactiver l'authentification par défaut pour le développement
				.httpBasic(httpBasic -> httpBasic.disable())
				.formLogin(form -> form.disable());

		return http.build();
	}
}