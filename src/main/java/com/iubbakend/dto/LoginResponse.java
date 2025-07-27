package com.iubbakend.dto;

import com.iubbakend.entity.Administrateur;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

	private boolean success;
	private String message;
	private AdministrateurInfo administrateur;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class AdministrateurInfo {
		private Long id;
		private String username;
		private String nom;
		private String prenom;
		private String email;
		private Administrateur.Role role;
		private Boolean premiereConnexion;
	}
}