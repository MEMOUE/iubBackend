package com.iubbakend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.List;
@Entity
@Table(name = "directeur")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Directeur extends BaseEntity {
	@NotBlank(message = "Le nom est obligatoire")
	@Column(nullable = false)
	private String nom;

	@NotBlank(message = "Le titre est obligatoire")
	@Column(nullable = false)
	private String titre;

	@Column(name = "photo_url")
	private String photoUrl;

	@Column(length = 500)
	private String experience;

	@ElementCollection
	@CollectionTable(name = "directeur_diplomes", joinColumns = @JoinColumn(name = "directeur_id"))
	@Column(name = "diplome")
	private List<String> diplomes;

	@Column(length = 2000, name = "message_bienvenue")
	private String messageBienvenue;

	@Column(length = 2000, name = "message_vision")
	private String messageVision;

	@Column(length = 2000, name = "message_engagements")
	private String messageEngagements;

	@Column(length = 2000, name = "message_etudiants")
	private String messageEtudiants;

	@Email(message = "Email invalide")
	private String email;

	private String telephone;

	@Column(length = 500)
	private String adresse;

	@Column(name = "linkedin_url")
	private String linkedinUrl;

	@Column(nullable = false)
	private Boolean actif = true;
}
