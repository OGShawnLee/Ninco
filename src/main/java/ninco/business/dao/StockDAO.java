package ninco.business.dao;

import ninco.business.dto.StoreDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ninco.business.dto.StockDTO;
import ninco.common.ExceptionHandler;
import ninco.common.InvalidFieldException;
import ninco.common.UserDisplayableException;
import ninco.db.DBConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class StockDAO extends DAOShape<StockDTO> {
  private static final Logger LOGGER = LogManager.getLogger(StockDAO.class);
  private static final String CREATE_ONE_QUERY = "INSERT INTO STOCK (product_id, store_id, quantity) VALUES (?, ?, ?)";
  private static final String GET_ALL_QUERY = "SELECT * FROM CompleteStockView";
  private static final String GET_ALL_BY_STORE = "SELECT * FROM CompleteStockView WHERE store_id = ?";
  private static final StockDAO INSTANCE = new StockDAO();

  private StockDAO() {
  }

  public static StockDAO getInstance() {
    return INSTANCE;
  }

  @Override
  public StockDTO getDTOInstanceFromResultSet(ResultSet resultSet) throws InvalidFieldException, SQLException {
    return new StockDTO(
      resultSet.getInt("product_id"),
      resultSet.getInt("store_id"),
      resultSet.getString("product_name"),
      resultSet.getString("store_name"),
      resultSet.getInt("quantity"),
      resultSet.getFloat("price"),
      resultSet.getTimestamp("created_at").toLocalDateTime()
    );
  }

  public void createOne(StockDTO stockDTO) throws UserDisplayableException {
    try (
      Connection connection = DBConnector.getInstance().getConnection();
      PreparedStatement statement = connection.prepareStatement(CREATE_ONE_QUERY);
    ) {
      statement.setInt(1, stockDTO.getIDProduct());
      statement.setInt(2, stockDTO.getIDStore());
      statement.setInt(3, stockDTO.getQuantity());

      statement.executeUpdate();
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible crear inventario.");
    }
  }

  public ArrayList<StockDTO> getAll() throws UserDisplayableException {
    ArrayList<StockDTO> stockDTOList = new ArrayList<>();

    try (
      Connection connection = DBConnector.getInstance().getConnection();
      PreparedStatement statement = connection.prepareStatement(GET_ALL_QUERY);
      ResultSet resultSet = statement.executeQuery()
    ) {

      while (resultSet.next()) {
        stockDTOList.add(createDTOInstanceFromResultSet(resultSet));
      }

      return stockDTOList;
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible cargar el inventario.");
    }
  }

  public ArrayList<StockDTO> getAllByStore(StoreDTO storeDTO) throws UserDisplayableException {
    ArrayList<StockDTO> stockDTOList = new ArrayList<>();

    try (
      Connection connection = DBConnector.getInstance().getConnection();
      PreparedStatement statement = connection.prepareStatement(GET_ALL_BY_STORE);
    ) {
      statement.setInt(1, storeDTO.getID());

      try (ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          stockDTOList.add(createDTOInstanceFromResultSet(resultSet));
        }
      }

      return stockDTOList;
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible cargar el inventario.");
    }
  }
}
