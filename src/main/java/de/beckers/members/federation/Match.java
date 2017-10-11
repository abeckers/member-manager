package de.beckers.members.federation;

import java.util.Date;

import lombok.Data;

@Data
public class Match {
	private boolean terminated;
	
	private String id;
	
	private String team;

	private String home;
	
	private String guest;
	
	private Date kickOff;
	
	private Score score;
}
