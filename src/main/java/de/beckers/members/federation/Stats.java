package de.beckers.members.federation;

import lombok.Data;

@Data
public class Stats {
	int win;
	
	int lost;
	
	int tie;

	public void win() {
		win++;
	}

	public void lost() {
		lost++;
	}

	public void tie() {
		tie++;
	}
	
	public String toString() {
		return win + "-" + lost + "-" + tie; 
	}
}
