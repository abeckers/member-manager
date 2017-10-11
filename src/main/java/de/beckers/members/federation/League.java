package de.beckers.members.federation;

import java.util.List;

import lombok.Data;

@Data
public class League {
	String name;

	String team;

	String shortName;

	List<Rank> ranking;

	List<Match> plan;
}
