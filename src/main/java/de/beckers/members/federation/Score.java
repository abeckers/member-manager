package de.beckers.members.federation;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class Score {
	Integer home;

	Integer guest;

	public String toString(int i) {
		String fmt = "%0" + i + "d:%0" + i + "d";
		return isDefined() ? String.format(fmt, home, guest) : "--:--";
	}

	@JsonIgnore
	public boolean isDefined() {
		return home != null;
	}
	
	@JsonIgnore
	public boolean isWin() {
		return isDefined() && home.intValue() > guest.intValue();
	}

	@JsonIgnore
	public boolean isLoss() {
		return isDefined() && home.intValue() < guest.intValue();
	}

	@JsonIgnore
	public boolean isTie() {
		return isDefined() && home.intValue() == guest.intValue();
	}
}
