    package org.example.mdintech.service.NotificationModule;

    import jakarta.mail.*;
    import jakarta.mail.internet.InternetAddress;
    import jakarta.mail.internet.MimeMessage;
    import java.util.Properties;
    import io.github.cdimascio.dotenv.Dotenv;


    public class mailNotificationService {

        private final String username;
        private final String password;

        public mailNotificationService() {
            Dotenv dotenv = Dotenv.load(); // Load environment variables from .env
            this.username = dotenv.get("EMAIL_USERNAME");
            this.password = dotenv.get("EMAIL_PASSWORD");
        }

        private Properties getMailProperties() {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            return props;
        }

        public void sendEmail(String recipient, String subject, String body) {
            Session session = Session.getInstance(getMailProperties(),
                    new Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                        }
                    });

            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(username));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
                message.setSubject(subject);
                message.setText(body);

                Transport.send(message);
                System.out.println("Email sent successfully to " + recipient);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }
