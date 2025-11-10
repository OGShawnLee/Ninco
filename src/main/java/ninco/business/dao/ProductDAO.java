package ninco.business.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ninco.business.dto.ProductDTO;
import ninco.common.ExceptionHandler;
import ninco.common.InvalidFieldException;
import ninco.common.UserDisplayableException;
import ninco.db.DBConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ProductDAO extends DAOShape<ProductDTO> {
  private static final Logger LOGGER = LogManager.getLogger(ProductDAO.class);
  private static final String CREATE_ONE_QUERY = "INSERT INTO Product (name, description, brand, price) VALUES (?, ?, ?, ?)";
  private static final String GET_ALL_QUERY = "SELECT * FROM CompleteProductView";
  private static final ProductDAO INSTANCE = new ProductDAO();

  private ProductDAO() {}

  public static ProductDAO getInstance() {
    return INSTANCE;
  }

  @Override
  public ProductDTO getDTOInstanceFromResultSet(ResultSet resultSet) throws InvalidFieldException, SQLException {
    return new ProductDTO(
      resultSet.getInt("product_id"),
      resultSet.getString("name"),
      resultSet.getString("description"),
      resultSet.getString("brand"),
      resultSet.getFloat("price"),
      resultSet.getInt("stock"),
      resultSet.getTimestamp("created_at").toLocalDateTime()
    );
  }

  public void createOne(ProductDTO storeDTO) throws UserDisplayableException {
    try (
      Connection connection = DBConnector.getInstance().getConnection();
      PreparedStatement statement = connection.prepareCall(CREATE_ONE_QUERY);
    ) {
      statement.setString(1, storeDTO.getName());
      statement.setString(2, storeDTO.getDescription());
      statement.setString(3, storeDTO.getBrand());
      statement.setFloat(4, storeDTO.getPrice());

      statement.executeUpdate();
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible crear producto.");
    }
  }

  public ArrayList<ProductDTO> getAll() throws UserDisplayableException {
    ArrayList<ProductDTO> storeDTOList = new ArrayList<>();

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
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible cargar los productos.");
    }
  }
}
