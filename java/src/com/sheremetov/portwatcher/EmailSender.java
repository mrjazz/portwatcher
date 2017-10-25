package com.sheremetov.portwatcher;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


class EmailSender {

    private final String settingsFile;

    EmailSender(String settingsFile) {
        this.settingsFile = settingsFile;
    }

    void send(String msg) {
        Properties props = new Properties();

        try {
            props.load(new FileInputStream(settingsFile));
        } catch (IOException e) {
            System.out.println(String.format("Setting file %s not found", settingsFile));
            System.exit(1);
        }

        final String username = props.getProperty("mail.from");
        final String password = props.getProperty("mail.pass");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            String sendTo = props.getProperty("mail.to");

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(sendTo));
            message.setSubject(props.getProperty("mail.subject"));
            message.setText(msg);

            System.out.println(String.format("Email to %s sent", sendTo));
            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}