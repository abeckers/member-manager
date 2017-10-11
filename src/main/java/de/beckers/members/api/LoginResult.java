package de.beckers.members.api;

import java.util.Set;

import de.beckers.members.model.MemberRole;
import lombok.Data;

@Data
public class LoginResult {
	public static enum State {
		OK,
		FAILED
	};
	
	private State state;
	
	private String key;
	
	private String userId;
	
	private Set<MemberRole> roles;
}
