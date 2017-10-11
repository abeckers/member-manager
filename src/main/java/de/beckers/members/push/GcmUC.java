package de.beckers.members.push;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpBackOffUnsuccessfulResponseHandler;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;

import de.beckers.members.federation.Match;
import de.beckers.members.federation.Update;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class GcmUC {
	private static final String GCM_URL = "https://android.googleapis.com/gcm/send";
	private static final String API_KEY = "AIzaSyD3Dg-tAx1r3tH5UCMbCvgG4g-HICJ-CI4";

	static final JsonFactory JSON_FACTORY = new JacksonFactory();

	private NetHttpTransport transport = new NetHttpTransport();

	private static final boolean SEND = true;
	
	@Value("${push.debug:false}")
	private boolean pushDebug;

	public boolean sendGcm(String topic, Match match, Update update) {
		String title = String.format(update.getType().getMessagePattern(),
				update.isHome() ? clean(match.getHome()) : clean(match.getGuest()));
		String to = topic + (pushDebug ? "-debug": "");
		log.info("send called " + to + " " + title);
		HttpRequestFactory requestFactory = transport.createRequestFactory(new HttpRequestInitializer() {
			@Override
			public void initialize(HttpRequest request) {
				request.setParser(new JsonObjectParser(JSON_FACTORY));
			}
		});

		GcmMessage gmsg = GcmMessage.builder().message(update.getMessage()).title(title)
				.score(update.getScore() == null ? null : update.getScore().toString(1)).team(match.getTeam()).build();
		GcmData gdata = GcmData.builder().message(gmsg).build();
		GcmRequest greq = GcmRequest.builder().to(to).data(gdata).time_to_live(86400).build();
		log.info("send request: " + greq);
		if (SEND) {
			try {
				HttpContent content = new JsonHttpContent(JSON_FACTORY, greq);
				GenericUrl url = new GenericUrl(GCM_URL);
				HttpRequest hreq = requestFactory.buildPostRequest(url, content);
				hreq.getHeaders().setAuthorization("key=" + API_KEY);
				hreq.setUnsuccessfulResponseHandler(
						new HttpBackOffUnsuccessfulResponseHandler(new ExponentialBackOff()));
				HttpResponse r = hreq.execute();
				GcmResponse resp = r.parseAs(GcmResponse.class);
				log.info("headers: " + r.getHeaders());
				log.info("status code: " + r.getStatusCode());
				log.info("response: " + resp);
			} catch (HttpResponseException e1) {
				if (e1.getStatusCode() == HttpStatus.UNAUTHORIZED.value()) {
					// TODO
				} else if (e1.getStatusCode() == HttpStatus.BAD_REQUEST.value()) {
					if ("INVALID_REGISTRATION".equals(e1.getMessage())) {
						log.warn("Invalid registration");
					}
				}
				log.warn("exception: " + e1.getStatusCode() + " " + e1.getMessage() + " " + e1.getContent());
			} catch (IOException e1) {
				log.error("", e1);
			}
		}
		return false;
	}

	private String clean(String guest) {
		return guest == null ? null : guest.replace("<b>", "").replace("</b>", "");
	}
}
