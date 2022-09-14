package com.autosetup.manager;

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
import org.springframework.stereotype.Service;

import com.autosetup.exception.ServiceException;
import com.autosetup.exception.ValidationException;
import com.autosetup.model.EmailRequest;

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

	@Value("${mail.replyto.address}")
	private String replyTo;

	final Configuration configuration;

	public EmailManager(Configuration configuration) {
		this.configuration = configuration;
	}

	public String sendEmail(Map<String, Object> emailContent, String subject, String templateFileName) {
		try {

			EmailRequest emailRequest = EmailRequest.builder().emailContent(emailContent)
					.toEmail(emailContent.get("toemail").toString()).subject(subject).templateFileName(templateFileName)
					.build();

			mimeMessage.setFrom(new InternetAddress(fromEmail));
			if (replyTo != null && !replyTo.isEmpty()) {
				String[] mailAddressTo = replyTo.split(",");
				InternetAddress[] mailAddress_TO = new InternetAddress[mailAddressTo.length];
				for (int i = 0; i < mailAddressTo.length; i++) {
					mailAddress_TO[i] = new InternetAddress(mailAddressTo[i]);
				}
				mimeMessage.setReplyTo(mailAddress_TO);
			}
			mimeMessage.setSubject(emailRequest.getSubject());

			if (emailRequest.getToEmail() != null && !emailRequest.getToEmail().isEmpty()) {
				String[] split = emailRequest.getToEmail().toString().split(",");
				InternetAddress[] addressTo = new InternetAddress[split.length];
				int i = 0;
				for (String string : split) {
					addressTo[i] = new InternetAddress(string);
					i++;
				}
				mimeMessage.setRecipients(Message.RecipientType.TO, addressTo);
			} else {
				throw new ValidationException("To email is null");
			}

			InternetAddress[] addressCC = new InternetAddress[1];
			int i = 0;
			if (emailContent.containsKey("ccemail") && emailContent.get("ccemail") != null
					&& !emailContent.get("ccemail").toString().isEmpty()) {
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
			return "Email Sent Success";
		} catch (MessagingException | IOException | TemplateException e) {
			log.error("Error in email sending :{}", e.getMessage());
			throw new ServiceException("Error in email sending :" + e.getMessage());
		}
	}

	String getEmailContent(EmailRequest emailRequest) throws IOException, TemplateException {
		StringWriter stringWriter = new StringWriter();
		configuration.getTemplate(emailRequest.getTemplateFileName()).process(emailRequest.getEmailContent(),
				stringWriter);
		return stringWriter.getBuffer().toString();
	}
}
