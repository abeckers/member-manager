package de.beckers.members.push;

import java.io.IOException;
import java.net.URLEncoder;

import org.springframework.stereotype.Component;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;

import de.beckers.members.federation.Match;
import de.beckers.members.federation.Update;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class Telegram {
	private static final String TELEGRAM_URL = "https://api.telegram.org/bot239669809:AAE8hSfHZLvMiQSeSJJmTWRRtLDruIYOEFg/sendMessage?chat_id=@TroisdorfJets&text=";

	private NetHttpTransport transport = new NetHttpTransport();

	public void send(Match match, Update update) {
		try {
			HttpRequestFactory requestFactory = transport.createRequestFactory();
			String title = String.format(update.getType().getMessagePattern(),
					update.isHome() ? clean(match.getHome()) : clean(match.getGuest()));
			String msg = match.getTeam() + " " + title + " " + update.getMessage() + " " + (update.getScore() == null ? null : "Score " + update.getScore().toString(1));
			GenericUrl url = new GenericUrl(TELEGRAM_URL + URLEncoder.encode(msg, "UTF-8"));
			HttpRequest hreq = requestFactory.buildGetRequest(url);
			hreq.execute();
		} catch (IOException e) {
			log.error("can't send to telegram", e);
		}
	}

	private String clean(String guest) {
		return guest == null ? null : guest.replace("<b>", "").replace("</b>", "");
	}
}
