package de.beckers.members.api;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class PrepareResult {
	@Data
	public static class NewParent {
		private String name;
		
		private String firstName;

		private String mobile;

		private String email;
	}
	
	@Data
	@EqualsAndHashCode(callSuper = true)
	public static class MatchingParent extends NewParent {
		private String id;
	}
	
	@Data
	@AllArgsConstructor
	public static class TeamInfo {
		private String id;
		
		private String name;
	}
	
	private NewParent newParent1;
	
	private NewParent newParent2;
	
	private MatchingParent matchingParent1;
	
	private MatchingParent matchingParent2;
	
    private Set<TeamInfo> teams;
}
