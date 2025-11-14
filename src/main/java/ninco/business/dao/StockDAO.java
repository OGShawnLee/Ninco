package ninco.business.dao;

import ninco.business.dto.StockDTO;
import ninco.common.ExceptionHandler;
import ninco.common.InvalidFieldException;
import ninco.common.UserDisplayableException;
import ninco.db.DBConnector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class StockDAO extends DAOShape<StockDTO> {
    private static final Logger LOGGER = LogManager.getLogger(StockDAO.class);
    private static final String GET_ALL_QUERY = "SELECT " +
            "s.product_id, s.store_id, " +
            "p.name AS product_name, " +
            "st.name AS store_name, " +
            "s.quantity " +
            "FROM stock s " +
            "INNER JOIN product p ON s.product_id = p.product_id " +
            "INNER JOIN store st ON s.store_id = st.store_id";
    private static final StockDAO INSTANCE = new StockDAO();

    private StockDAO() {}

    public static StockDAO getInstance() { return INSTANCE; }

    @Override
    public StockDTO getDTOInstanceFromResultSet(ResultSet resultSet) throws InvalidFieldException, SQLException {
        return new StockDTO(
                resultSet.getInt("product_id"),
                resultSet.getInt("store_id"),
                resultSet.getString("product_name"),
                resultSet.getString("store_name"),
                resultSet.getInt("quantity"),
                resultSet.getTimestamp("created_at").toLocalDateTime()
        );
    }

    public ArrayList<StockDTO> getAll() throws UserDisplayableException {
        ArrayList<StockDTO> stockDTOList = new ArrayList<>();

        try (Connection connection = DBConnector.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_ALL_QUERY);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                stockDTOList.add(createDTOInstanceFromResultSet(resultSet));
            }
            return stockDTOList;
        } catch (SQLException e) {
            throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible cargar el inventario");
        }
    }
}
