package de.beckers.members.federation;

import lombok.Data;

@Data
public class Rank {
	int position;
	
	String team;
	
	Score score;
	
	int diff;
	
	Stats stats;
}
