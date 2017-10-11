package de.beckers.members.federation;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import au.id.jericho.lib.html.Element;
import au.id.jericho.lib.html.Source;

public class FederationResultsParser {

	// curl http://www.afcvnrw.de/cms/verband/spielbetrieb/liga-tabellenaspiele.html > liga.html
	public static void main(String[] args) throws Exception {
		String content = getHtml();
		FederationResultsParser parser = new FederationResultsParser();
		DateFormat df3 = new SimpleDateFormat("dd.MM.yy");
		League u19 = parser.process("U19 Regionalliga NRW", content, "U19");
		printLeague(df3, u19);
		League u16 = parser.process("U16 Regionalliga NRW - Gruppe West", content, "U16");
		printLeague(df3, u16);
		League u13 = parser.process("U13 Oberliga NRW - Gruppe West", content, "u13");
		printLeague(df3, u13);
		League seniors = parser.process("Regionalliga NRW", content, "seniors", "> Regionalliga NRW");
		printLeague(df3, seniors);
	}

	private static void printLeague(DateFormat df3, League league) {
		System.out.println("------------------------------------------------------------");
		if (league == null) {
			System.out.println("Liga nicht gefunden");
		}
		else {
			printRanking(df3, league.getRanking());
			System.out.println("------------------------------------------------------------");
			printPlan(df3, league.getPlan());
		}
	}

	private static void printPlan(DateFormat df3, List<Match> plan) {
		DateFormat df = new SimpleDateFormat("dd.MM.yy - HH:mm");
		DateFormat df2 = new SimpleDateFormat("MMMMM");
		System.out.println("<b>DATUM</b>|<b>HEIM</b>|<b>GAST</b>|<b>ERGEBNIS</b>");
		System.out.println("|||");
		int oldMonth = 0;
		for (Match match : plan) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(match.getKickOff());
			int newMonth = cal.get(Calendar.MONTH);
			if (oldMonth != newMonth) {
				System.out.println("<b>" + df2.format(match.getKickOff()) + "|||</b>");
				oldMonth = newMonth;
			}
			System.out.println(df.format(match.getKickOff()) + "|" + match.getHome() + "|" + match.getGuest() + "|"
					+ (match.getScore() == null ? "--:--" : match.getScore().toString(2)));

		}
		System.out.println("(Stand:&nbsp;" + df3.format(new Date()) + ")|||");
	}

	public League process(String devsion, String content, String shortName) throws Exception {
		return process(devsion, content, shortName, devsion);
	}
	
	public League process(String devision, String content, String shortName, String matcher) throws Exception {
		int start = content.indexOf(matcher);

		int tbStart = content.indexOf("<table", start);
		if (tbStart == -1) {
			return null;
		}
		
		int tbEnd = content.indexOf("</table", tbStart);
		if (tbEnd == -1) {
			return null;
		}

		String table = content.substring(tbStart, tbEnd + 8);
		List<Rank> ranking = readRanking(table);

		League league = new League();
		league.setName(devision);
		league.setShortName(shortName);

		for (Rank rank : ranking) {
			if (rank.getTeam().contains("Jets")) {
				league.setTeam(rank.getTeam());
			}
		}

		league.setRanking(ranking);

		int planStart = content.indexOf("<table", tbEnd);
		int planEnd = content.indexOf("</table", planStart);

		List<Match> plan = readPlan(shortName, content.substring(planStart, planEnd + 8));
		league.setPlan(plan);
		int oldMonth = 0;
		for (Match match : plan) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(match.getKickOff());
			int newMonth = cal.get(Calendar.MONTH);
			if (oldMonth != newMonth) {
				oldMonth = newMonth;
			}
			Rank rHome = findRanking(match.getHome(), ranking);
			Rank rGuest = findRanking(match.getGuest(), ranking);
			if (rHome != null && rGuest != null && match.getScore() != null) {
				if (match.getScore().isWin()) {
					rHome.getStats().win();
					rGuest.getStats().lost();
				} else if (match.getScore().isLoss()) {
					rHome.getStats().lost();
					rGuest.getStats().win();
				} else {
					rHome.getStats().tie();
					rGuest.getStats().tie();
				}
			}
		}
		return league;
	}

	private Rank findRanking(String team, List<Rank> ranking) {
		for (Rank rank : ranking) {
			if (rank.getTeam().equals(team)) {
				return rank;
			}
		}
		return null;
	}

	private static void printRanking(DateFormat df3, List<Rank> ranking) {
		System.out.println("<b>TP</b>|<b>VEREIN</b>|<b>SCORES</b>|<b>DIFF</b>|<b>W-L-T</b>");
		System.out.println("||||");
		for (Rank rank : ranking) {
			System.out.println(rank.getPosition() + "|" + rank.getTeam() + "|" + rank.getScore().toString(3) + "|"
					+ formatDiff(rank.getDiff()) + "|" + rank.getStats());
		}
		System.out.println("|(Stand: " + df3.format(new Date()) + ")|||");
	}

	private static String getHtml() throws IOException, UnsupportedEncodingException {
		byte[] encoded = Files.readAllBytes(Paths.get("/Users/abeckers/jets/src/team2/liga.html"));
		String content = new String(encoded, "UTF-8");
		return content;
	}

	public FederationResultsParser() {

	}

	public static List<Match> readPlan(String team, String xml) throws Exception {
		DateFormat df = new SimpleDateFormat("dd.MM.yy - HH:mm");
		Source source = new Source(xml);
		List<Element> elems = source.findAllElements("tr");
		elems.remove(0);
		List<Match> matches = new ArrayList<Match>();
		for (Element element : elems) {
			List<Element> tds = element.findAllElements("td");
			Match match = new Match();
			match.setTeam(team);
			Date kickOff = df.parse(tds.get(0).extractText());
			String id = tds.get(1).extractText();
			String teams = tds.get(3).extractText();
			String[] t = teams.split(" vs. ");
			match.setId(id);
			match.setKickOff(kickOff);
			match.setHome(fixTeamName(t[0]));
			match.setGuest(fixTeamName(t[1]));
			String scores = tds.get(4).extractText();
			if (!scores.equals("n/a")) {
				match.setScore(parseScore(scores));
				match.setTerminated(true);
			}
			matches.add(match);
			// System.out.println(kickOff + "|" + teams + " | " + scores + " ->
			// " + match);
		}
		return matches;
	}

	private static Score parseScore(String scores) {
		String[] t = scores.split(":");
		int v1 = Integer.parseInt(t[0].trim());
		int v2 = Integer.parseInt(t[1].trim());
		Score score = new Score();
		score.setHome(v1);
		score.setGuest(v2);
		return score;
	}

	private static String fixTeamName(String name) {
		boolean u19 = name.contains("U19");
		boolean u16 = name.contains("U16");
		boolean u13 = name.contains("U13");
		boolean prospects = name.contains("Troisdorf Jets II");
		boolean flags = name.contains("Troisdorf Jets SF");
		name = name.replace(" U19", "");
		name = name.replace(" U16", "");
		name = name.replace(" U13", "");
		name = name.replace(" SF", "");
		if (prospects) {
			name = name.replace(" II", "");
		}
		String jets = u19 ? "<b>JuniorJets</b>"
				: (u16 ? "<b>RookieJets</b>" : (prospects ? "<b>JetsProspects</b>" : (u13 ? "<b>FutureJets</b>" : (flags ? "<b>FlagJets</b>" : "<b>JetsSeniors</b>"))));
		name = name.replace("Troisdorf Jets", jets);
		return name;
	}

	public static List<Rank> readRanking(String xml) throws Exception {
		List<Rank> r = new ArrayList<>();
		Source source = new Source(xml);
		List<Element> elems = source.findAllElements("tr");
		elems.remove(0);
		for (Element element : elems) {
			List<Element> tds = element.findAllElements("td");
			String ranking = tds.get(0).extractText();
			String team = tds.get(1).extractText();

			Rank rank = new Rank();
			rank.setTeam(fixTeamName(team));
			rank.setDiff(Integer.parseInt(tds.get(5).extractText()));
			rank.setPosition(Integer.parseInt(ranking));
			rank.setScore(parseScore(tds.get(4).extractText()));
			rank.setStats(new Stats());
			r.add(rank);
		}
		return r;
	}

	private static String formatDiff(int diff) {
		String fmt = "%03d";
		if (diff < 0) {
			fmt = "%04d";
		}
		return (diff > 0 ? "+" : "") + String.format(fmt, diff);
	}
}
