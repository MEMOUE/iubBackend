package com.iubbakend.controller;

import com.iubbakend.entity.Actualite;
import com.iubbakend.entity.Directeur;
import com.iubbakend.service.ActualiteService;
import com.iubbakend.service.DirecteurService;
import jakarta.validation.Valid;
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

	private final ActualiteService actualiteService;
	private final DirecteurService directeurService;

	// Types de fichiers autorisés
	private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");
	private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

	@PostMapping("/actualite")
	public ResponseEntity<Actualite> uploadActualite(
			@RequestPart("actualite") @Valid Actualite actualite,
			@RequestPart("file") MultipartFile file
	) {
		log.info("Upload d'image pour actualité: {}", actualite.getTitre());

		return handleFileUpload(
				file,
				actualiteUploadDir,
				"/uploads/actualites/",
				"actualite",
				(path) -> {
					actualite.setImageUrl(path);
					return actualiteService.save(actualite);
				}
		);
	}

	@PostMapping("/directeur")
	public ResponseEntity<Directeur> uploadDirecteur(
			@RequestPart("directeur") @Valid Directeur directeur,
			@RequestPart("file") MultipartFile file
	) {
		log.info("Upload de photo pour directeur: {}", directeur.getNom());

		return handleFileUpload(
				file,
				directeurUploadDir,
				"/uploads/directeurs/",
				"directeur",
				(path) -> {
					directeur.setPhotoUrl(path);
					return directeurService.save(directeur);
				}
		);
	}

	private <T> ResponseEntity<T> handleFileUpload(
			MultipartFile file,
			String uploadDir,
			String urlPrefix,
			String entityType,
			java.util.function.Function<String, T> saveEntity
	) {
		try {
			// Validation du fichier
			if (file.isEmpty()) {
				log.error("Fichier vide reçu pour {}", entityType);
				return ResponseEntity.badRequest().build();
			}

			// Vérification de la taille
			if (file.getSize() > MAX_FILE_SIZE) {
				log.error("Fichier trop volumineux: {} bytes pour {}", file.getSize(), entityType);
				return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).build();
			}

			// Validation de l'extension
			String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
			String extension = getFileExtension(originalFilename).toLowerCase();

			if (!ALLOWED_EXTENSIONS.contains(extension)) {
				log.error("Extension de fichier non autorisée: {} pour {}", extension, entityType);
				return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build();
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

			// Sauvegarde de l'entité avec l'URL du fichier
			T savedEntity = saveEntity.apply(fileUrl);

			log.info("Upload réussi pour {}: {}", entityType, filename);
			return ResponseEntity.status(HttpStatus.CREATED).body(savedEntity);

		} catch (IOException e) {
			log.error("Erreur I/O lors de l'upload pour {}: {}", entityType, e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		} catch (Exception e) {
			log.error("Erreur inattendue lors de l'upload pour {}: {}", entityType, e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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