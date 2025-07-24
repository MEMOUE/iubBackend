package com.iubbakend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${cors.allowed-origins}")
public class UploadController {

	@Value("${app.upload.actualites.dir}")
	private String actualiteUploadDir;

	@Value("${app.upload.directeurs.dir}")
	private String directeurUploadDir;

	// Types de fichiers autorisés
	private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");
	private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

	@PostMapping("/actualite/image")
	public ResponseEntity<Map<String, Object>> uploadActualiteImage(@RequestParam("file") MultipartFile file) {
		log.info("Upload d'image pour actualité: {}", file.getOriginalFilename());

		return handleFileUpload(file, actualiteUploadDir, "/uploads/actualites/", "actualite");
	}

	@PostMapping("/directeur/photo")
	public ResponseEntity<Map<String, Object>> uploadDirecteurPhoto(@RequestParam("file") MultipartFile file) {
		log.info("Upload de photo pour directeur: {}", file.getOriginalFilename());

		return handleFileUpload(file, directeurUploadDir, "/uploads/directeurs/", "directeur");
	}

	private ResponseEntity<Map<String, Object>> handleFileUpload(
			MultipartFile file,
			String uploadDir,
			String urlPrefix,
			String entityType
	) {
		Map<String, Object> response = new HashMap<>();

		try {
			// Validation du fichier
			if (file.isEmpty()) {
				log.error("Fichier vide reçu pour {}", entityType);
				response.put("error", "Fichier vide");
				return ResponseEntity.badRequest().body(response);
			}

			// Vérification de la taille
			if (file.getSize() > MAX_FILE_SIZE) {
				log.error("Fichier trop volumineux: {} bytes pour {}", file.getSize(), entityType);
				response.put("error", "Fichier trop volumineux (max 5MB)");
				return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(response);
			}

			// Validation de l'extension
			String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
			String extension = getFileExtension(originalFilename).toLowerCase();

			if (!ALLOWED_EXTENSIONS.contains(extension)) {
				log.error("Extension de fichier non autorisée: {} pour {}", extension, entityType);
				response.put("error", "Type de fichier non autorisé. Utilisez: " + String.join(", ", ALLOWED_EXTENSIONS));
				return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(response);
			}

			// Génération d'un nom de fichier unique
			String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
			String uniqueId = UUID.randomUUID().toString().substring(0, 8);
			String filename = entityType + "_" + timestamp + "_" + uniqueId + "." + extension;

			// Création du chemin de destination
			Path uploadPath = Paths.get(uploadDir);
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
				log.info("Dossier créé: {}", uploadPath.toAbsolutePath());
			}

			Path filePath = uploadPath.resolve(filename);

			// Sauvegarde du fichier
			Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
			log.info("Fichier sauvegardé: {}", filePath.toAbsolutePath());

			// URL d'accès au fichier
			String fileUrl = urlPrefix + filename;

			// Réponse de succès
			response.put("url", fileUrl);
			response.put("filename", filename);
			response.put("originalName", originalFilename);
			response.put("size", file.getSize());
			response.put("message", "Fichier uploadé avec succès");

			log.info("Upload réussi pour {}: {}", entityType, filename);
			return ResponseEntity.status(HttpStatus.CREATED).body(response);

		} catch (IOException e) {
			log.error("Erreur I/O lors de l'upload pour {}: {}", entityType, e.getMessage());
			response.put("error", "Erreur lors de la sauvegarde du fichier");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		} catch (Exception e) {
			log.error("Erreur inattendue lors de l'upload pour {}: {}", entityType, e.getMessage());
			response.put("error", "Erreur inattendue lors de l'upload");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	/**
	 * Extrait l'extension d'un nom de fichier
	 */
	private String getFileExtension(String filename) {
		if (filename == null || filename.isEmpty()) {
			return "";
		}
		int lastDotIndex = filename.lastIndexOf('.');
		return lastDotIndex > 0 ? filename.substring(lastDotIndex + 1) : "";
	}
}