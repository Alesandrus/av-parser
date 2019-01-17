package ru.alesandrus.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.alesandrus.utils.DateUtils;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

/**
 * @author Alexander Ivanov
 * @version 1.0
 * @since 15.01.2019
 */
@Component
public class AdvertisementSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdvertisementSender.class);
    private static final Properties properties;
    private static final String SEND_TO = "raulon7@mail.ru";
    private static final String AVITO_PARSER_MAIL = "web@gmail.com";
    private static final String PASSWORD = "1234";

    static {
        properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
    }

    public void sendReport(String filename, String creationTime) {
        Session session = getSession();
        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(AVITO_PARSER_MAIL));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO,new InternetAddress(SEND_TO));

            // Set Subject: header field
            message.setSubject(String.format("Отчет за %s", creationTime));

            // Create the message part
            BodyPart messageBodyPart = new MimeBodyPart();

            // Fill the message
            messageBodyPart.setText("Отчет находится во вложении");

            // Create a multipar message
            Multipart multipart = new MimeMultipart();

            // Set text message part
            multipart.addBodyPart(messageBodyPart);

            // Part two is attachment
            messageBodyPart = new MimeBodyPart();
            DataSource source = new FileDataSource(filename);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(filename.substring(filename.lastIndexOf(System.lineSeparator())));
            multipart.addBodyPart(messageBodyPart);

            // Send the complete message parts
            message.setContent(multipart );

            // Send message
            Transport.send(message);
            LOGGER.info("Sent message successfully");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

    private Session getSession() {
        return Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(AVITO_PARSER_MAIL, PASSWORD);
            }
        });
    }
}
