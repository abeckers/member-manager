package de.beckers.members.federation;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ResultsStore {
	private Map<String, League> leagues = null;

	@Value("${jets.results.file.name}")
	private String datafile;

	public void init() {
		if (leagues != null) {
			return;
		}
		leagues = new HashMap<String, League>();
		File in = new File(datafile);
		if (in.exists()) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				League[] ll = mapper.readValue(in, League[].class);
				for (League league : ll) {
					leagues.put(league.getShortName(), league);
				} 
			} catch (IOException e) {
				log.warn("can't read results file", e);
			}
		}
	}

	public void fetchResults() {
		init();
		RestTemplate tmpl = new RestTemplate();
		String result = tmpl.getForObject("http://www.afcvnrw.de/cms/verband/spielbetrieb/liga-tabellenaspiele.html",
				String.class);
		FederationResultsParser parser = new FederationResultsParser();

		merge(parser, "Regionalliga NRW", result, "seniors", "> Regionalliga NRW");
		merge(parser, "Verbandsliga NRW - Gruppe West", result, "prospects");
		merge(parser, "U19 Oberliga NRW", result, "u19");
		merge(parser, "U16 Regionalliga NRW - Gruppe West", result, "u16");
		merge(parser, "U13 Oberliga NRW - Gruppe West", result, "u13");
		merge(parser, "Senioren Flag Liga NRW", result, "flags");
		File out = new File(datafile);
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writeValue(out, leagues.values());
		} catch (IOException e) {
			log.warn("can't read results file", e);
		}
	}

	private void merge(FederationResultsParser parser, String league, String result, String id, String search) {
		try {
			merge(id, parser.process(league, result, id, search));
		} catch (Exception e) {
			log.warn("can't parse seniors");
		}
	}

	private void merge(String id, League league) {
		if (league == null) {
			return;
		}
		if (leagues.get(id) == null) {
			leagues.put(id, league);
		} else {
			merge(leagues.get(id), league);
		}
	}

	private void merge(League current, League update) {
		if (current == null || update == null) {
			return;
		}
		current.setRanking(update.getRanking());
		current.setTeam(update.getTeam());
		List<Match> currentPlan = current.getPlan();
		List<Match> updatePlan = update.getPlan();
		current.setPlan(updatePlan);
		for (Match currentMatch : currentPlan) {
			Match updateMatch = findMatch(updatePlan, currentMatch.getId());
			if (updateMatch != null && !updateMatch.isTerminated()) {
				updateMatch.setScore(currentMatch.getScore());
				updateMatch.setTerminated(currentMatch.isTerminated());
			}
		}
	}

	private Match findMatch(List<Match> plan, String id) {
		for (Match match : plan) {
			if (match.getId().equals(id)) {
				return match;
			}
		}
		return null;
	}

	private void merge(FederationResultsParser parser, String league, String result, String id) {
		try {
			merge(id, parser.process(league, result, id));
		} catch (Exception e) {
			log.warn("can't parse " + id, e);
		}
	}

	public Collection<League> getAllLeagues() {
		init();
		return leagues.values();
	}

	public League getLeague(String team) {
		init();
		return leagues.get(team);
	}

	public Match findMatch(String matchId) {
		init();
		if (leagues == null || matchId == null) {
			return null;
		}
		for (League league : leagues.values()) {
			for (Match match : league.getPlan()) {
				if (matchId.equals(match.getId())) {
					return match;
				}
			}
		}
		return null;
	}
}
