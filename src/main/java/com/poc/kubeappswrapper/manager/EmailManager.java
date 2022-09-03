package com.poc.kubeappswrapper.manager;

import com.poc.kubeappswrapper.model.EmailRequest;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
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

@Service
public class EmailManager {

    @Autowired
    private MimeMessage mimeMessage;

    @Value("${mail.from.address}")
    private String fromEmail;

    @Value("${mail.to}")
    private String toEmail;

    final Configuration configuration;

    public EmailManager(Configuration configuration) {
        this.configuration = configuration;
    }

    public ResponseEntity<String> sendEmail(Map<String, Object> emailContent, String subject, String templateFileName) {
        try {
            EmailRequest emailRequest = EmailRequest.builder()
                    .emailContent(emailContent)
                    .toEmail(toEmail)
                    .subject(subject)
                    .templateFileName(templateFileName)
                    .build();
            mimeMessage.setFrom(new InternetAddress(fromEmail));
            mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(emailRequest.getToEmail()));
            mimeMessage.setSubject(emailRequest.getSubject());

            String data = getEmailContent(emailRequest);
            mimeMessage.setContent(data, "text/html; charset=utf-8"); // as "text/plain"
            mimeMessage.setSentDate(new Date());
            Transport.send(mimeMessage);
            return new ResponseEntity<>("Email Sent Success", HttpStatus.OK);
        } catch (MessagingException | IOException | TemplateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    String getEmailContent(EmailRequest emailRequest) throws IOException, TemplateException {
        StringWriter stringWriter = new StringWriter();
        configuration.getTemplate(emailRequest.getTemplateFileName()).process(emailRequest.getEmailContent(), stringWriter);
        return stringWriter.getBuffer().toString();
    }
}
