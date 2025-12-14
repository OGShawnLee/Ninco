package ninco.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class EmailService {
  private static final Logger LOGGER = LogManager.getLogger(EmailService.class);
  private static EmailService instance;
  private final String SENDER_EMAIL;
  private final String SENDER_PASSWORD;
  // NOTE: Change the path if the project is running in a different environment
  private static final String MAIL_PROPERTIES_FILE = "src/main/resources/mail.properties";

  private EmailService() throws UserDisplayableException {
    Properties properties = new Properties();

    try (FileInputStream input = new FileInputStream(MAIL_PROPERTIES_FILE)) {
      properties.load(input);

      this.SENDER_EMAIL = properties.getProperty("mail.email");
      this.SENDER_PASSWORD = properties.getProperty("mail.password");

      handlePropertiesVerification();
    } catch (FileNotFoundException e) {
      throw handleConfigurationFileNotFound(e);
    } catch (IOException e) {
      throw handleIOException(e);
    }
  }

  private UserDisplayableException handleIOException(IOException e) {
    return ExceptionHandler.handleIOException(LOGGER, e, "No ha sido posible cargar la configuración del servicio de email.");
  }

  private UserDisplayableException handleConfigurationFileNotFound(FileNotFoundException e) {
    LOGGER.fatal("No se ha encontrado el archivo de configuración del servicio de email: mail.properties", e);
    return new UserDisplayableException(
      "No se ha encontrado el archivo de configuración del servicio de email. Por favor, comuníquese con el desarrollador del sistema."
    );
  }

  private void handlePropertiesVerification() throws UserDisplayableException {
    if (SENDER_EMAIL == null || SENDER_PASSWORD == null) {
      LOGGER.fatal("Las propiedades de conexión al servicio de email no están configuradas correctamente. Revisar mail.properties.");
      throw new UserDisplayableException(
        "Las propiedades de conexión al servicio de email no están configuradas correctamente. Por favor, comuníquese con el desarrollador del sistema."
      );
    }
  }

  public void sendVerificationCode(String recipientEmail, String code) throws UserDisplayableException {
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
      message.setText(
        String.format(
          "Su código de verificación para completar el registro es: %s.\n\nEste código expira en 15 minutos.",
          code
        )
      );

      Transport.send(message);
    } catch (MessagingException e) {
      throw new UserDisplayableException("No se pudo enviar el correo de verificación. Verifique su conexión.", e);
    }
  }

  public static synchronized EmailService getInstance() throws UserDisplayableException {
    if (instance == null) {
      instance = new EmailService();
    }

    return instance;
  }

}
