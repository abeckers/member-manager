package de.beckers.members.api;

import org.joda.time.LocalDate;

import lombok.Data;

@Data
public class RegistrationInfo {
	public static enum ParentState {
		NONE,
		NEW,
		SET
	}
	
	private ParentState state1;

	private ParentState state2;
	
	private String parent1Id;
	
	private String parent2Id;
	
	private String teamId;
	
	private LocalDate registrationDate;
}
