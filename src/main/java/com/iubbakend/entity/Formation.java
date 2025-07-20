package com.iubbakend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;
@Entity
@Table(name = "formations")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Formation extends BaseEntity {
	@NotBlank(message = "Le nom est obligatoire")
	@Column(nullable = false)
	private String nom;

	@NotBlank(message = "Le diplôme est obligatoire")
	@Column(nullable = false)
	private String diplome; // Licence, Master

	@NotBlank(message = "La durée est obligatoire")
	@Column(nullable = false)
	private String duree;

	@NotBlank(message = "La catégorie est obligatoire")
	@Column(nullable = false)
	private String categorie; // licence, master

	@Column(length = 1000)
	private String description;

	@ElementCollection
	@CollectionTable(name = "formation_objectifs", joinColumns = @JoinColumn(name = "formation_id"))
	@Column(name = "objectif")
	private List<String> objectifs;

	@ElementCollection
	@CollectionTable(name = "formation_debouches", joinColumns = @JoinColumn(name = "formation_id"))
	@Column(name = "debouche")
	private List<String> debouches;

	@Column(name = "frais_scolarite", precision = 10, scale = 2)
	private BigDecimal fraisScolarite;

	@Column(name = "conditions_admission", length = 1000)
	private String conditionsAdmission;

	@Column(name = "programme_detaille", length = 2000)
	private String programmeDetaille;

	@Column(name = "nombre_semestres")
	private Integer nombreSemestres;

	@Column(name = "nombre_places")
	private Integer nombrePlaces;

	@Column(name = "nombre_inscrits")
	private Integer nombreInscrits = 0;

	@Column(nullable = false)
	private Boolean disponible = true;

	@Column(nullable = false)
	private Boolean actif = true;
}
