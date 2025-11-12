package ninco.business.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ninco.business.dto.StoreDTO;
import ninco.common.ExceptionHandler;
import ninco.common.InvalidFieldException;
import ninco.common.UserDisplayableException;
import ninco.db.DBConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class StoreDAO extends DAOShape<StoreDTO> {
  private static final Logger LOGGER = LogManager.getLogger(StoreDAO.class);
  private static final String CREATE_ONE_QUERY = "INSERT INTO Store (name, address, phone) VALUES (?, ?, ?)";
  private static final String GET_ALL_QUERY = "SELECT * FROM CompleteStoreView";
  private static final String FIND_ONE_QUERY_BY_PHONE_NUMBER = "SELECT * FROM CompleteStoreView WHERE phone = ?";
  private static final String UPDATE_ONE_QUERY =
    "UPDATE Store set name = ?, address = ?, phone = ? WHERE store_id = ?";
  private static final StoreDAO INSTANCE = new StoreDAO();

  private StoreDAO() {}

  public static StoreDAO getInstance() {
    return INSTANCE;
  }

  @Override
  public StoreDTO getDTOInstanceFromResultSet(ResultSet resultSet) throws InvalidFieldException, SQLException {
    return new StoreDTO(
      resultSet.getInt("store_id"),
      resultSet.getString("name"),
      resultSet.getString("address"),
      resultSet.getString("phone"),
      resultSet.getTimestamp("created_at").toLocalDateTime(),
      resultSet.getInt("employee_count")
    );
  }

  public void createOne(StoreDTO storeDTO) throws UserDisplayableException {
    try (
      Connection connection = DBConnector.getInstance().getConnection();
      PreparedStatement statement = connection.prepareCall(CREATE_ONE_QUERY);
    ) {
      statement.setString(1, storeDTO.getName());
      statement.setString(2, storeDTO.getAddress());
      statement.setString(3, storeDTO.getPhoneNumber());

      statement.executeUpdate();
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible crear empleado.");
    }
  }

  public StoreDTO findOneByPhoneNumber(String phoneNumber) throws UserDisplayableException {
    try (
      Connection connection = DBConnector.getInstance().getConnection();
      PreparedStatement statement = connection.prepareStatement(FIND_ONE_QUERY_BY_PHONE_NUMBER)
    ) {
      statement.setString(1, phoneNumber);

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

  public ArrayList<StoreDTO> getAll() throws UserDisplayableException {
    ArrayList<StoreDTO> storeDTOList = new ArrayList<>();

    try (
      Connection connection = DBConnector.getInstance().getConnection();
      PreparedStatement statement = connection.prepareStatement(GET_ALL_QUERY);
      ResultSet resultSet = statement.executeQuery()
    ) {
      while (resultSet.next()) {
        storeDTOList.add(createDTOInstanceFromResultSet(resultSet));
      }

      return storeDTOList;
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible cargar las tiendas.");
    }
  }

  public boolean updateOne(StoreDTO storeDTO, StoreDTO originalStoreDTO) throws UserDisplayableException {
    try (
      Connection connection = DBConnector.getInstance().getConnection();
      PreparedStatement statement = connection.prepareCall(UPDATE_ONE_QUERY);
    ) {
      statement.setString(1, storeDTO.getName());
      statement.setString(2, storeDTO.getAddress());
      statement.setString(3, storeDTO.getPhoneNumber());
      statement.setInt(4, originalStoreDTO.getID());

      statement.executeUpdate();

      boolean failed = statement.getUpdateCount() == -1;

      if (failed) {
        LOGGER.warn(
          "Actualización Fallida Inesperada de Tienda: {}",
          String.format("%s %s", storeDTO.getID(), storeDTO.getName())
        );
      }

      return failed;
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible actualizar producto.");
    }
  }
}
