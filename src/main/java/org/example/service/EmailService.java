package org.example.service;

import org.example.util.LoggerUtil;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Service class for sending emails using SMTP.
 */
public class EmailService {
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "465";
    private final String senderEmail;     // Email address to send from
    private final String senderPassword; // Password for sender email

    /**
     * Constructs an EmailService with sender credentials.
     * @param senderEmail The sender's email address
     * @param senderPassword The sender's email password
     * @throws IllegalArgumentException if credentials are invalid
     */

    public EmailService(String senderEmail, String senderPassword) {
        if (senderEmail == null || senderEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("Sender email must not be null or empty");
        }
        if (!senderEmail.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new IllegalArgumentException("Invalid sender email format");
        }
        if (senderPassword == null || senderPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Sender password must not be null or empty");
        }
        this.senderEmail = senderEmail;
        this.senderPassword = senderPassword;
    }


    /**
     * Sends an email to the specified recipient.
     * @param recipientEmail The recipient's email address
     * @param subject The email subject
     * @param body The email body content
     * @return true if email sent successfully, false otherwise
     */
    public boolean sendEmail(String recipientEmail, String subject, String body) {
        validateEmailParameters(recipientEmail, subject, body);

        Properties properties = configureSmtpProperties();
        Session session = createMailSession(properties);

        return sendMessage(session, recipientEmail, subject, body);
    }

    /**
     * Validates email parameters before sending.
     */
    private void validateEmailParameters(String recipientEmail, String subject, String body) {
        if (recipientEmail == null || recipientEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("Recipient email is required");
        }
        if (subject == null || subject.trim().isEmpty()) {
            throw new IllegalArgumentException("Email subject is required");
        }
        if (body == null || body.trim().isEmpty()) {
            throw new IllegalArgumentException("Email body is required");
        }
    }

    /**
     * Configures SMTP properties for email sending.
     */
    Properties configureSmtpProperties() {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", SMTP_HOST);
        properties.put("mail.smtp.port", SMTP_PORT);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        return properties;
    }

    /**
     * Creates a mail session with authentication.
     */
    Session createMailSession(Properties properties) {
        return Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });
    }

    /**
     * Creates and sends the email message.
     */
    boolean sendMessage(Session session, String recipientEmail, String subject, String body) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
            message.setSubject(subject);
            message.setText(body);
            Transport.send(message);
            LoggerUtil.logInfo("Email sent successfully to " + recipientEmail);
            return true;
        } catch (MessagingException e) {
            LoggerUtil.logError("Failed to send email to " + recipientEmail, e);
            return false;
        }
    }
}
