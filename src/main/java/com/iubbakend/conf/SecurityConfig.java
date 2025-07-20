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
						.requestMatchers("/api/**").permitAll()

						// Autoriser Swagger UI et documentation
						.requestMatchers("/swagger-ui/**").permitAll()
						.requestMatchers("/swagger-ui.html").permitAll()
						.requestMatchers("/api-docs/**").permitAll()
						.requestMatchers("/v3/api-docs/**").permitAll()

						// Autoriser les ressources statiques
						.requestMatchers("/css/**", "/js/**", "/images/**").permitAll()

						// Toutes les autres requêtes nécessitent une authentification
						.anyRequest().authenticated()
				)

				// Désactiver l'authentification par défaut pour le développement
				.httpBasic(httpBasic -> httpBasic.disable())
				.formLogin(form -> form.disable());

		return http.build();
	}
}