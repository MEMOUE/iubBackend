package com.iubbakend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "actualites")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Actualite extends BaseEntity {
	@NotBlank(message = "Le titre est obligatoire")
	@Column(nullable = false)
	private String titre;

	@Column(length = 2000)
	private String description;

	@Column(length = 5000)
	private String contenu;

	@Column(name = "image_url")
	private String imageUrl;

	@Column(name = "date_publication")
	private LocalDate datePublication;

	@Column(name = "date_evenement")
	private LocalDate dateEvenement;

	private String categorie; // evenement, news, ceremonie, etc.

	private String auteur;

	@Column(name = "nombre_vues")
	private Integer nombreVues = 0;

	@Column(nullable = false)
	private Boolean publie = false;

	@Column(nullable = false)
	private Boolean actif = true;
}
