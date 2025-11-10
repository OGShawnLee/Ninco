package ninco.business.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ninco.business.dto.AccountDTO;
import ninco.business.enumeration.Role;
import ninco.business.enumeration.State;
import ninco.common.ExceptionHandler;
import ninco.common.InvalidFieldException;
import ninco.common.UserDisplayableException;
import ninco.db.DBConnector;

public class AccountDAO extends DAOShape<AccountDTO> {
  private static final Logger LOGGER = LogManager.getLogger(AccountDAO.class);
  private static final String GET_ONE_QUERY = "SELECT * FROM Account WHERE email = ?";
  private static final String IS_THERE_ONE_ADMIN_QUERY = "SELECT COUNT(*) FROM Account WHERE role = 'ADMIN' LIMIT 1";
  private static AccountDAO INSTANCE = new AccountDAO();

  private AccountDAO() {}

  public static AccountDAO getInstance() {
    return INSTANCE;
  }

  @Override
  public AccountDTO getDTOInstanceFromResultSet(ResultSet resultSet) throws InvalidFieldException, SQLException {
    return new AccountDTO(
      resultSet.getInt("account_id"),
      resultSet.getString("email"),
      resultSet.getString("password"),
      Role.valueOf(resultSet.getString("role")),
      State.valueOf(resultSet.getString("state")),
      resultSet.getTimestamp("created_at").toLocalDateTime()
    );
  }

  public AccountDTO findOne(String email) throws InvalidFieldException, UserDisplayableException {
    try (
      Connection connection = DBConnector.getInstance().getConnection();
      PreparedStatement statement = connection.prepareStatement(GET_ONE_QUERY)
    ) {
      statement.setString(1, email);

      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return createDTOInstanceFromResultSet(resultSet);
        }
      }

      return null;
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible cargar la cuenta.");
    }
  }

  public boolean isThereAnAdminAccount() throws UserDisplayableException {
    try (
      Connection connection = DBConnector.getInstance().getConnection();
      PreparedStatement statement = connection.prepareStatement(IS_THERE_ONE_ADMIN_QUERY);
      ResultSet resultSet = statement.executeQuery()
    ) {
      if (resultSet.next()) {
        return resultSet.getInt(1) > 0;
      }

      return false;
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible verificar la existencia de una cuenta de administrador.");
    }
  }
}
