package services;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailService {

    // Méthode pour envoyer un email
    public static void sendEmail(String recipient, String subject, String content) {
        // Informations du compte SMTP (remplace avec tes propres identifiants)
        final String senderEmail = "ines.rahrah@esprit.tn"; // Remplace par ton email
        final String senderPassword = "stgp xfjw biea dfjm";   // Remplace par ton mot de passe

        // Configuration des propriétés SMTP
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        // Création de la session
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            // Création du message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject(subject);
            message.setText(content);

            // Envoi de l'email
            Transport.send(message);
            System.out.println("Email envoyé avec succès à " + recipient);

        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'envoi de l'email !");
        }
    }
}
