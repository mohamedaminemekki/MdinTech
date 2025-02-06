package org.example.mdintech.test;

import org.example.mdintech.service.MailerModule.mailNotificationService;

public class MailNotificationTest {
    public static void main(String[] args) {
        // Create an instance of the MailNotificationService
        mailNotificationService mailer = new mailNotificationService();

        // Define test recipient, subject, and message
        String recipient = "mohamedanas.moncer@esprit.tn";  // Replace with a valid email
        String subject = "Test Email from Java";
        String message = "Hello! This is a test email sent from the MailNotificationService.";

        // Send email
        mailer.sendEmail(recipient, subject, message);
    }
}
