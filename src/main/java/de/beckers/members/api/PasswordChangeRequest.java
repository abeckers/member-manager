package de.beckers.members.api;

import lombok.Data;

@Data
public class PasswordChangeRequest {
	private String oldPassword;

	private String newPassword;
}
