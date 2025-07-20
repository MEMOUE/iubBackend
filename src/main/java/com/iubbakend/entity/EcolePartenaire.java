package com.iubbakend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.List;
@Entity
@Table(name = "ecoles_partenaires")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class EcolePartenaire extends BaseEntity {
	@NotBlank(message = "Le nom est obligatoire")
	@Column(nullable = false)
	private String nom;

	@NotBlank(message = "Le pays est obligatoire")
	@Column(nullable = false)
	private String pays;

	@NotBlank(message = "La ville est obligatoire")
	@Column(nullable = false)
	private String ville;

	@NotBlank(message = "Le type est obligatoire")
	@Column(nullable = false)
	private String type;

	@NotBlank(message = "La région est obligatoire")
	@Column(nullable = false)
	private String region; // europe, amerique, afrique

	@Column(length = 1000)
	private String description;

	@ElementCollection
	@CollectionTable(name = "ecole_domaines", joinColumns = @JoinColumn(name = "ecole_id"))
	@Column(name = "domaine")
	private List<String> domaines;

	@ElementCollection
	@CollectionTable(name = "ecole_programmes", joinColumns = @JoinColumn(name = "ecole_id"))
	@Column(name = "programme")
	private List<String> programmes;

	@ElementCollection
	@CollectionTable(name = "ecole_avantages", joinColumns = @JoinColumn(name = "ecole_id"))
	@Column(name = "avantage")
	private List<String> avantages;

	@Column(name = "duree_partenariat")
	private String dureePartenariat;

	@Column(name = "site_web")
	private String siteWeb;

	@Column(name = "email_contact")
	private String emailContact;

	@Column(nullable = false)
	private Boolean actif = true;
}
