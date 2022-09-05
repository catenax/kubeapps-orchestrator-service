package com.poc.kubeappswrapper.manager;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;
import java.util.Map;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.poc.kubeappswrapper.exception.ValidationException;
import com.poc.kubeappswrapper.model.EmailRequest;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailManager {

	@Autowired
	private MimeMessage mimeMessage;

	@Value("${mail.from.address}")
	private String fromEmail;

	final Configuration configuration;

	public EmailManager(Configuration configuration) {
		this.configuration = configuration;
	}

	public ResponseEntity<String> sendEmail(Map<String, Object> emailContent, String subject, String templateFileName) {
		try {

			EmailRequest emailRequest = EmailRequest.builder().emailContent(emailContent)
					.toEmail(emailContent.get("toemail").toString()).subject(subject).templateFileName(templateFileName)
					.build();

			mimeMessage.setFrom(new InternetAddress(fromEmail));
			mimeMessage.setSubject(emailRequest.getSubject());

			if (emailRequest.getToEmail() != null) {
				String[] split = emailRequest.getToEmail().toString().split(",");
				InternetAddress[] addressTo = new InternetAddress[split.length];
				int i = 0;
				for (String string : split) {
					addressTo[i] = new InternetAddress(string);
				}
				mimeMessage.setRecipients(Message.RecipientType.TO, addressTo);
			} else {
				throw new ValidationException("To email is null");
			}

			InternetAddress[] addressCC = new InternetAddress[1];
			int i = 0;
			if (emailContent.containsKey("ccemail")) {
				String[] split = emailContent.get("ccemail").toString().split(",");
				addressCC = new InternetAddress[split.length + 1];
				for (String string : split) {
					addressCC[i] = new InternetAddress(string);
					i++;
				}
			}
			addressCC[i] = new InternetAddress(fromEmail);

			mimeMessage.setRecipients(Message.RecipientType.CC, addressCC);

			String data = getEmailContent(emailRequest);
			mimeMessage.setContent(data, "text/html; charset=utf-8"); // as "text/plain"
			mimeMessage.setSentDate(new Date());
			Transport.send(mimeMessage);
			return new ResponseEntity<>("Email Sent Success", HttpStatus.OK);
		} catch (MessagingException | IOException | TemplateException e) {
			log.error("Error in email sending :{}", e.getMessage());
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	String getEmailContent(EmailRequest emailRequest) throws IOException, TemplateException {
		StringWriter stringWriter = new StringWriter();
		configuration.getTemplate(emailRequest.getTemplateFileName()).process(emailRequest.getEmailContent(),
				stringWriter);
		return stringWriter.getBuffer().toString();
	}
}
