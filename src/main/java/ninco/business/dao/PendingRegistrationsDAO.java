package ninco.business.dao;

import ninco.business.dto.PendingRegistrationDTO;
import ninco.common.ExceptionHandler;
import ninco.common.UserDisplayableException;
import ninco.db.DBConnector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

public class PendingRegistrationsDAO {
  private static final Logger LOGGER = LogManager.getLogger(PendingRegistrationsDAO.class);
  private static final PendingRegistrationsDAO INSTANCE = new PendingRegistrationsDAO();
  // este es para actualizar el pin por si el usuario lo pide de nuevo
  private static final String UPSERT_QUERY =
    "INSERT INTO PendingRegistrations (email, pin, expires_at, password, role) " +
      "VALUES (?, ?, ?, ?, ?) " +
      "ON DUPLICATE KEY UPDATE pin = VALUES(pin), expires_at = VALUES(expires_at), password = VALUES(password), role = VALUES(role), created_at = NOW(6)";
  private static final String VERIFY_QUERY =
    "SELECT count(*) " +
      "FROM PendingRegistrations " +
      "WHERE email = ? AND pin = ? AND expires_at > NOW(6)";

  private PendingRegistrationsDAO() {}

  public static PendingRegistrationsDAO getInstance() {
    return INSTANCE;
  }

  public void save(PendingRegistrationDTO dto) throws UserDisplayableException {
    try (Connection connection = DBConnector.getInstance().getConnection();
         PreparedStatement statement = connection.prepareStatement(UPSERT_QUERY)) {

      statement.setString(1, dto.getEmail());
      statement.setString(2, dto.getPin());
      statement.setTimestamp(3, Timestamp.valueOf(dto.getExpiresAt()));
      statement.setString(4, dto.getPassword());
      statement.setString(5, dto.getRole().toString());

      statement.executeUpdate();
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "Error al guardar el registro de auditoría.");
    }
  }

  public boolean verifyPin(String email, String pin) throws UserDisplayableException {
    try (Connection connection = DBConnector.getInstance().getConnection();
         PreparedStatement statement = connection.prepareStatement(VERIFY_QUERY)) {

      statement.setString(1, email);
      statement.setString(2, pin);

      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return resultSet.getInt(1) > 0;
        }
      }
      return false;
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "Error al verificar el PIN.");
    }
  }
}
