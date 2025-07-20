package com.iubbakend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.List;
@Entity
@Table(name = "entreprises_partenaires")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class EntreprisePartenaire extends BaseEntity {
	@NotBlank(message = "Le nom est obligatoire")
	@Column(nullable = false)
	private String nom;

	@NotBlank(message = "Le secteur est obligatoire")
	@Column(nullable = false)
	private String secteur; // telecoms, banque, technologie, industrie

	@NotBlank(message = "La taille est obligatoire")
	@Column(nullable = false)
	private String taille; // PME, Grande entreprise

	@NotBlank(message = "La localisation est obligatoire")
	@Column(nullable = false)
	private String localisation;

	@Column(name = "type_partenariat")
	private String typePartenariat;

	@Column(length = 1000)
	private String description;

	@ElementCollection
	@CollectionTable(name = "entreprise_collaborations", joinColumns = @JoinColumn(name = "entreprise_id"))
	@Column(name = "collaboration")
	private List<String> collaborations;

	@ElementCollection
	@CollectionTable(name = "entreprise_postes", joinColumns = @JoinColumn(name = "entreprise_id"))
	@Column(name = "poste")
	private List<String> postes;

	@ElementCollection
	@CollectionTable(name = "entreprise_avantages", joinColumns = @JoinColumn(name = "entreprise_id"))
	@Column(name = "avantage")
	private List<String> avantages;

	@Column(name = "duree_partenariat")
	private String dureePartenariat;

	@Column(name = "site_web")
	private String siteWeb;

	@Column(name = "email_contact")
	private String emailContact;

	@Column(name = "responsable_contact")
	private String responsableContact;

	@Column(nullable = false)
	private Boolean actif = true;
}
