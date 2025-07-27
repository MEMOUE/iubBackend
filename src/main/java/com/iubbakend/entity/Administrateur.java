package com.iubbakend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "administrateurs")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Administrateur extends BaseEntity {

	@NotBlank(message = "Le nom d'utilisateur est obligatoire")
	@Column(nullable = false, unique = true)
	private String username;

	@NotBlank(message = "Le mot de passe est obligatoire")
	@Column(nullable = false)
	private String password;

	@NotBlank(message = "Le nom est obligatoire")
	@Column(nullable = false)
	private String nom;

	@NotBlank(message = "Le prénom est obligatoire")
	@Column(nullable = false)
	private String prenom;

	@Email(message = "Email invalide")
	@NotBlank(message = "L'email est obligatoire")
	@Column(nullable = false, unique = true)
	private String email;

	private String telephone;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role = Role.ADMIN;

	@Column(nullable = false)
	private Boolean actif = true;

	@Column(name = "premiere_connexion")
	private Boolean premiereConnexion = true;

	public enum Role {
		ADMIN, SUPER_ADMIN
	}
}