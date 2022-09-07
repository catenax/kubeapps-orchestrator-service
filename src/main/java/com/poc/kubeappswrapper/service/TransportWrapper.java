package com.poc.kubeappswrapper.service;


import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class TransportWrapper {
    public void send(MimeMessage msg) throws MessagingException {
        javax.mail.Transport.send(msg);
    }
}
