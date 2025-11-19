package ninco.common;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailService {
    // TODO: Mover a archivo de configuración en producción
    private static final String SENDER_EMAIL = "tu_correo@gmail.com";
    private static final String SENDER_PASSWORD = "tu_contraseña_de_aplicacion";

    public static void sendVerificationCode(String recipientEmail, String code) throws UserDisplayableException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Ninco ERP - Verificación de Cuenta");
            message.setText("Su código de verificación para completar el registro es: " + code + "\n\nEste código expira en 15 minutos.");

            Transport.send(message);
        } catch (MessagingException e) {
            throw new UserDisplayableException("No se pudo enviar el correo de verificación. Verifique su conexión.", e);
        }
    }
}
