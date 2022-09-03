package com.poc.kubeappswrapper.config;

import java.util.Properties;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailConfig {

    @Value("${mail.smtp.host}")
    private String host;

    @Value("${mail.smtp.port}")
    private String port;

    @Value("${mail.from.address}")
    private String fromAddress;

    @Value("${mail.smtp.starttls.enable}")
    private Boolean startTlsEnable;

    @Value("${mail.smtp.auth}")
    private Boolean auth;

    @Bean
    public MimeMessage mimeMessage() {
        // Get the Session object.// and pass username and password
        Session session = Session.getInstance(properties(), new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("Data_Intelligence_Hub_Login", "P/r8}rf5q)/Wr1gn");
            }
        });
        return new MimeMessage(session);
    }

    @Bean
    public Properties properties() {
        Properties props = new Properties();
        props.put("mail.smtp.user", "Data_Intelligence_Hub_Login");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.starttls.enable", startTlsEnable);
        props.put("mail.smtp.auth", auth);
        props.put("mail.debug", "true");
        return props;
    }
}