package de.beckers.members.federation;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class Update {
	public static enum Type {
		INFO("Information"),

		TOUCHDOWN("Touchdown für %s!"),

		PAT("PAT für %s!"),

		TPC("TPC für %s!"),

		SAFETY("Safety für %s!"),

		FIELD_GOAL("Field Goal für %s!"),

        KICKOFF("Das Spiel beginnt!"),

        Q2("2. Quarter beginnt"),

        HALFTIME("Jetzt ist Halbzeit!"),

        Q3("3. Quarter beginnt"),

        Q4("4. Quarter beginnt"),

        RESULT("Spiel beendet!");

		private final String messagePattern;

		private Type(String messagePattern) {
			this.messagePattern = messagePattern;
		}

		public String getMessagePattern() {
			return messagePattern;
		}

	}

	@NotNull
	private Type type;

	private boolean home;

	private Score score;

	private String message;
}
