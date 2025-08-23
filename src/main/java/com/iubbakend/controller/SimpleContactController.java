package com.iubbakend.controller;

import com.iubbakend.service.SimpleEmailService;
import com.iubbakend.service.SimpleEmailService.ContactFormData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${cors.allowed-origins}")
public class SimpleContactController {

	private final SimpleEmailService emailService;

	/**
	 * 📩 Endpoint pour envoyer un message de contact
	 */
	@PostMapping("/send")
	public ResponseEntity<Map<String, Object>> sendContact(@Valid @RequestBody ContactRequest request) {
		log.info("📩 Nouveau message de contact de: {} {}", request.firstName, request.lastName);

		Map<String, Object> response = new HashMap<>();

		try {
			// Convertir la requête en données pour l'email
			ContactFormData formData = new ContactFormData();
			formData.setFirstName(request.firstName);
			formData.setLastName(request.lastName);
			formData.setEmail(request.email);
			formData.setPhone(request.phone);
			formData.setDepartment(request.department);
			formData.setSubject(request.subject);
			formData.setMessage(request.message);

			// Envoyer l'email
			boolean emailSent = emailService.sendContactToSecretariat(formData);

			if (emailSent) {
				response.put("success", true);
				response.put("message", "Votre message a été envoyé avec succès. Nous vous répondrons dans les plus brefs délais.");
				log.info("✅ Message envoyé avec succès de: {}", formData.getFullName());
				return ResponseEntity.ok(response);
			} else {
				response.put("success", false);
				response.put("message", "Une erreur est survenue lors de l'envoi. Veuillez réessayer ou nous contacter directement.");
				log.warn("⚠️ Échec envoi email pour: {}", formData.getFullName());
				return ResponseEntity.status(500).body(response);
			}

		} catch (Exception e) {
			log.error("❌ Erreur lors du traitement du message de contact: {}", e.getMessage(), e);
			response.put("success", false);
			response.put("message", "Une erreur technique est survenue. Veuillez réessayer plus tard.");
			return ResponseEntity.status(500).body(response);
		}
	}

	/**
	 * Classe pour la requête de contact
	 */
	public static class ContactRequest {
		@NotBlank(message = "Le prénom est obligatoire")
		public String firstName;

		@NotBlank(message = "Le nom est obligatoire")
		public String lastName;

		@Email(message = "Email invalide")
		@NotBlank(message = "L'email est obligatoire")
		public String email;

		public String phone;

		@NotBlank(message = "Le département est obligatoire")
		public String department;

		@NotBlank(message = "Le sujet est obligatoire")
		public String subject;

		@NotBlank(message = "Le message est obligatoire")
		public String message;
	}
}