package com.iubbakend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class SimpleEmailService {

	private final JavaMailSender mailSender;

	@Value("${app.email.from:noreply@iub-university.com}")
	private String fromEmail;

	@Value("${app.email.secretariat:secretariat@iub-university.com}")
	private String secretariatEmail;

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm");

	/**
	 * Envoie le message du formulaire de contact au secrétariat
	 */
	public boolean sendContactToSecretariat(ContactFormData formData) {
		try {
			log.info("📧 Envoi message de contact au secrétariat de: {}", formData.getFullName());

			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			helper.setFrom(fromEmail);
			helper.setTo(secretariatEmail);
			helper.setReplyTo(formData.getEmail()); // Pour répondre directement au visiteur
			helper.setSubject("📩 Nouveau message - " + formData.getSubject());

			String htmlContent = buildEmailContent(formData);
			helper.setText(htmlContent, true);

			mailSender.send(message);
			log.info("✅ Email envoyé avec succès au secrétariat");
			return true;

		} catch (MessagingException e) {
			log.error("❌ Erreur lors de l'envoi de l'email: {}", e.getMessage(), e);
			return false;
		}
	}

	/**
	 * Construit le contenu HTML de l'email
	 */
	private String buildEmailContent(ContactFormData formData) {
		return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f5f5f5; }
                    .container { max-width: 600px; margin: 0 auto; background: white; padding: 30px; border-radius: 10px; }
                    .header { background: #B85450; color: white; padding: 20px; text-align: center; margin: -30px -30px 20px -30px; }
                    .field { margin: 15px 0; padding: 10px; background: #f9f9f9; border-left: 4px solid #B85450; }
                    .label { font-weight: bold; color: #333; }
                    .message-box { background: #f0f8ff; padding: 15px; border-radius: 5px; margin: 20px 0; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h2>📩 Nouveau Message de Contact</h2>
                        <p>International University of Bouake</p>
                    </div>
                    
                    <div class="field">
                        <div class="label">👤 Nom complet:</div>
                        <div>%s</div>
                    </div>
                    
                    <div class="field">
                        <div class="label">📧 Email:</div>
                        <div>%s</div>
                    </div>
                    
                    <div class="field">
                        <div class="label">📱 Téléphone:</div>
                        <div>%s</div>
                    </div>
                    
                    <div class="field">
                        <div class="label">🏢 Département:</div>
                        <div>%s</div>
                    </div>
                    
                    <div class="field">
                        <div class="label">📝 Sujet:</div>
                        <div>%s</div>
                    </div>
                    
                    <div class="field">
                        <div class="label">🕒 Reçu le:</div>
                        <div>%s</div>
                    </div>
                    
                    <div class="message-box">
                        <div class="label">💬 Message:</div>
                        <div style="margin-top: 10px;">%s</div>
                    </div>
                    
                    <p><strong>💡 Pour répondre, cliquez simplement sur "Répondre" dans votre logiciel de messagerie.</strong></p>
                </div>
            </body>
            </html>
            """,
				formData.getFullName(),
				formData.getEmail(),
				formData.getPhone() != null ? formData.getPhone() : "Non renseigné",
				formData.getDepartment(),
				formData.getSubject(),
				LocalDateTime.now().format(DATE_FORMATTER),
				formData.getMessage().replace("\n", "<br>")
		);
	}

	/**
	 * Classe pour les données du formulaire
	 */
	@lombok.Data
	public static class ContactFormData {
		private String firstName;
		private String lastName;
		private String email;
		private String phone;
		private String department;
		private String subject;
		private String message;

		public String getFullName() {
			return firstName + " " + lastName;
		}
	}
}