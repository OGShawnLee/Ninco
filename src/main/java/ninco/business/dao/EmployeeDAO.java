package ninco.business.dao;

import ninco.business.dto.AccountDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;

import ninco.business.dto.EmployeeDTO;
import ninco.business.enumeration.Role;
import ninco.business.enumeration.State;
import ninco.common.ExceptionHandler;
import ninco.common.InvalidFieldException;
import ninco.common.UserDisplayableException;
import ninco.db.DBConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

public class EmployeeDAO extends DAOShape<EmployeeDTO> {
  private static final Logger LOGGER = LogManager.getLogger(EmployeeDAO.class);
  private static final String CREATE_ONE_QUERY =
    "INSERT INTO Employee (email, name, last_name, store_id) VALUES (?, ?, ?, ?)";
  private static final String CREATE_ONE_ACCOUNT_QUERY =
    "INSERT INTO Account (email, password, role) VALUES (?, ?, ?)";
  private static final String FIND_ONE_QUERY = "SELECT * FROM CompleteEmployeeView WHERE email = ?";
  private static final String GET_ALL_QUERY = "SELECT * FROM CompleteEmployeeView";
  private static final String UPDATE_ONE_QUERY =
    "UPDATE Employee SET name = ?, last_name = ?, store_id = ? WHERE employee_id = ?";
  private static final String UPDATE_ONE_ACCOUNT_QUERY = "UPDATE Account SET email = ?, role = ?, state = ? WHERE account_id = ?";
  private static EmployeeDAO INSTANCE = new EmployeeDAO();

  private EmployeeDAO() {
  }

  public static EmployeeDAO getInstance() {
    return INSTANCE;
  }

  @Override
  public EmployeeDTO getDTOInstanceFromResultSet(ResultSet resultSet) throws InvalidFieldException, SQLException {
    return new EmployeeDTO(
      resultSet.getInt("employee_id"),
      resultSet.getInt("account_id"),
      resultSet.getInt("store_id"),
      resultSet.getString("store_name"),
      resultSet.getString("email"),
      Role.valueOf(resultSet.getString("role")),
      State.valueOf(resultSet.getString("state")),
      resultSet.getString("name"),
      resultSet.getString("last_name"),
      resultSet.getTimestamp("created_at").toLocalDateTime()
    );
  }

  public void createOne(EmployeeDTO employeeDTO, String password) throws UserDisplayableException {
    try (
      Connection connection = DBConnector.getInstance().getConnection();
      PreparedStatement employeeStatement = connection.prepareCall(CREATE_ONE_QUERY);
      PreparedStatement accountStatement = connection.prepareCall(CREATE_ONE_ACCOUNT_QUERY)
    ) {
      connection.setAutoCommit(false);

      employeeStatement.setString(1, employeeDTO.getEmail());
      employeeStatement.setString(2, employeeDTO.getName());
      employeeStatement.setString(3, employeeDTO.getLastName());
      if (employeeDTO.getIDStore() >= 0) {
        employeeStatement.setInt(4, employeeDTO.getIDStore());
      } else {
        employeeStatement.setNull(4, Types.INTEGER);
      }

      accountStatement.setString(1, employeeDTO.getEmail());
      accountStatement.setString(2, EmployeeDAO.createHashedPassword(password));
      accountStatement.setString(3, employeeDTO.getRole().toString());

      accountStatement.executeUpdate();
      employeeStatement.executeUpdate();
      connection.commit();
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible crear empleado.");
    }
  }

  public EmployeeDTO findOne(String email) throws UserDisplayableException {
    try (
      Connection connection = DBConnector.getInstance().getConnection();
      PreparedStatement statement = connection.prepareStatement(FIND_ONE_QUERY)
    ) {
      statement.setString(1, email);

      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return createDTOInstanceFromResultSet(resultSet);
        }
      }

      return null;
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible cargar el empleado.");
    }
  }

  public ArrayList<EmployeeDTO> getAll() throws UserDisplayableException {
    ArrayList<EmployeeDTO> employeeDTOList = new ArrayList<>();

    try (
      Connection connection = DBConnector.getInstance().getConnection();
      PreparedStatement statement = connection.prepareStatement(GET_ALL_QUERY);
      ResultSet resultSet = statement.executeQuery()
    ) {
      while (resultSet.next()) {
        employeeDTOList.add(createDTOInstanceFromResultSet(resultSet));
      }

      return employeeDTOList;
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible cargar los empleados.");
    }
  }

  public void updateOne(EmployeeDTO employeeDTO) throws UserDisplayableException {
    try (
      Connection connection = DBConnector.getInstance().getConnection();
      PreparedStatement employeeStatement = connection.prepareStatement(UPDATE_ONE_QUERY);
      PreparedStatement accountStatement = connection.prepareStatement(UPDATE_ONE_ACCOUNT_QUERY);
    ) {
      connection.setAutoCommit(false);

      accountStatement.setString(1, employeeDTO.getEmail());
      accountStatement.setString(2, employeeDTO.getRole().toString());
      accountStatement.setString(3, employeeDTO.getState().toString());
      accountStatement.setInt(4, employeeDTO.getIDAccount());

      employeeStatement.setString(1, employeeDTO.getName());
      employeeStatement.setString(2, employeeDTO.getLastName());
      employeeStatement.setInt(3, employeeDTO.getIDStore());
      employeeStatement.setInt(4, employeeDTO.getID());

      accountStatement.executeUpdate();
      employeeStatement.executeUpdate();
      connection.commit();
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible actualizar empleado.");
    }
  }

  private static String createHashedPassword(String password) {
    return BCrypt.hashpw(password, BCrypt.gensalt());
  }
}
