package de.beckers.members.federation;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.beckers.members.federation.Update.Type;
import de.beckers.members.push.GcmUC;
import de.beckers.members.push.Telegram;

@RestController
public class ResultsController {
	@Autowired
	private ResultsStore store;

	@Autowired
	private GcmUC gcm;
	
	@Autowired
	private Telegram telegram;

	@RequestMapping(value = "/results", method = RequestMethod.GET)
	public Collection<League> resultsAll() {
		return store.getAllLeagues();
	}

	@RequestMapping(value = "/results/{team}", method = RequestMethod.GET)
	public League resultsTeam(@PathVariable("team") String team) {
		return store.getLeague(team);
	}

	@RequestMapping(value = "/results/{team}/ranking", method = RequestMethod.GET)
	public List<Rank> rankingTeam(@PathVariable("team") String team) {
		return store.getLeague(team).getRanking();
	}

	@RequestMapping(value = "/results/{team}/plan", method = RequestMethod.GET)
	public List<Match> planTeam(@PathVariable("team") String team) {
		return store.getLeague(team).getPlan();
	}

	@RequestMapping(value = "/ticker/{matchId}", method = RequestMethod.POST)
	public Match ticker(@PathVariable("matchId") String matchId, @RequestBody @Validated Update update) {
		matchId = matchId.replace("-", "/");
		Match match = store.findMatch(matchId);
		if (match != null && !match.isTerminated()) {
			match.setScore(update.getScore());
			gcm.sendGcm("/topics/" + "ticker-" + match.getTeam(), match, update);
			telegram.send(match, update);
		}
		if (update.getType() == Type.RESULT) {
			match.setTerminated(true);
		}
		return match;
	}
}
