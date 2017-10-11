package de.beckers.members;

import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import de.beckers.members.federation.ResultsStore;
import de.beckers.members.model.Registration;
import de.beckers.members.model.RegistrationState;
import de.beckers.members.repository.RegistrationRepository;

@Component
public class Scheduler {
	private static final Logger LOG = LoggerFactory.getLogger(Scheduler.class);

	@Autowired
	private RegistrationRepository regRep;

	@Value("${jets.mail.enabled:true}")
	private boolean enabled;
	
	@Value("${jets.mail.from.address}")
	private String mailFromAddress;

	@Value("${jets.mail.from.name}")
	private String mailFromName;
	
	@Value("${jets.mail.debug:false}")
	private boolean debug;

	@Autowired
	private JavaMailSender mail;
	
	@Autowired
	private ResultsStore res;

	@Scheduled(cron = "15 * * * * *")
	public void sendMails() {
		if (!enabled) {
			return;
		}
			
		for (Registration reg : regRep.findByState(RegistrationState.NEW, new PageRequest(0, 100))) {
			sendMail(reg);
		}
	}
	
	@Scheduled(cron = "0 */5 * * * *")
	public void fetchResults() {
		res.fetchResults();
	}

	private void sendMail(Registration reg) {
		System.out.println("should send " + reg.getEmail());
		try {
	        ((JavaMailSenderImpl) mail).setProtocol("smtps");
	        
			MimeMessage msg = mail.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(msg, true);
			String rcpt = debug ? "abeckers@freenet.de" : reg.getEmail();
			System.out.println(rcpt);
			helper.addTo(rcpt, reg.getName() + ", " + reg.getFirstName());
			// helper.addTo(reg.getEmail(), reg.getName() + ", " +
			// reg.getFirstName());
			helper.setFrom(mailFromAddress, mailFromName);
			helper.setSubject("Anmeldung bei den Troisdorf Jets");
			helper.setText(
					"Bitte klicke auf den folgenden Link, um die E-Mail-Adresse zu bestätigen und das Anmeldeformular auszudrucken.\n\nhttps://team2-jets.rhcloud.com/confirm/"
							+ reg.getId() + "\n\nMit sportlichem Gruß,\nDie Troisdorf Jets",
					"<html><head></head><body><p>Bitte klicke auf den folgenden Link, um die E-Mail-Adresse zu bestätigen und das Anmeldeformular auszudrucken.</p><p><a href=\"https://team2-jets.rhcloud.com/confirm/"
							+ reg.getId()
							+ "\" >E-Mail-Adresse bestätigen</a></p><p>Mit sportlichem Gruß,</p><p>Die Troisdorf-Jets</p></body></html>");
			mail.send(msg);
			LOG.info("mail sent to " + reg.getEmail());
			reg.setState(RegistrationState.EMAIL_SENT);
			regRep.save(reg);
		} catch (Exception e) {
			LOG.error("mail not sent to " + reg.getEmail(), e);
			reg.setState(RegistrationState.EMAIL_ERROR);
			regRep.save(reg);
		}
	}
}
