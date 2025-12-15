package ninco.business.dao;

import ninco.business.dto.CartItemDTO;
import ninco.common.ExceptionHandler;
import ninco.common.UserDisplayableException;
import ninco.db.DBConnector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.List;

public class InvoiceDAO {
  private static final Logger LOGGER = LogManager.getLogger(InvoiceDAO.class);
  private static final InvoiceDAO INSTANCE = new InvoiceDAO();

  private static final String INSERT_INVOICE = "INSERT INTO Invoice (store_id, name_client) VALUES (?, ?)";
  private static final String INSERT_SALE = "INSERT INTO Sale (invoice_id, store_id, employee_id, product_id, amount, price) VALUES (?, ?, ?, ?, ?, ?)";
  private static final String UPDATE_STOCK = "UPDATE Stock SET quantity = quantity - ? WHERE store_id = ? AND product_id = ?";

  private InvoiceDAO() {
  }

  public static InvoiceDAO getInstance() {
    return INSTANCE;
  }

  public void createInvoiceTransaction(int storeId, int employeeId, String clientName, List<CartItemDTO> items) throws UserDisplayableException {
    Connection connection = null;
    PreparedStatement stmtInvoice = null;
    PreparedStatement stmtSale = null;
    PreparedStatement stmtStock = null;
    ResultSet generatedKeys = null;

    try {
      connection = DBConnector.getInstance().getConnection();
      connection.setAutoCommit(false);

      stmtInvoice = connection.prepareStatement(INSERT_INVOICE, Statement.RETURN_GENERATED_KEYS);
      stmtInvoice.setInt(1, storeId);
      stmtInvoice.setString(2, clientName);
      stmtInvoice.executeUpdate();

      generatedKeys = stmtInvoice.getGeneratedKeys();
      if (!generatedKeys.next()) {
        throw new SQLException("Error creando factura, no se obtuvo ID.");
      }
      int invoiceId = generatedKeys.getInt(1);

      stmtSale = connection.prepareStatement(INSERT_SALE);
      stmtStock = connection.prepareStatement(UPDATE_STOCK);

      for (CartItemDTO item : items) {
        int productId = item.getProduct().getIDProduct();
        int quantity = item.getQuantity();
        float price = item.getPrice();

        stmtSale.setInt(1, invoiceId);
        stmtSale.setInt(2, storeId);
        stmtSale.setInt(3, employeeId);
        stmtSale.setInt(4, productId);
        stmtSale.setInt(5, quantity);
        stmtSale.setFloat(6, price);
        stmtSale.addBatch();

        stmtStock.setInt(1, quantity);
        stmtStock.setInt(2, storeId);
        stmtStock.setInt(3, productId);
        stmtStock.addBatch();
      }

      stmtSale.executeBatch();
      stmtStock.executeBatch();

      connection.commit();

    } catch (SQLException e) {
      try {
        connection.rollback();
      } catch (SQLException ex) {
        LOGGER.error("Rollback failed", ex);
      }
      if (e.getSQLState() != null && e.getSQLState().startsWith("23")) {
        throw new UserDisplayableException("No hay suficiente stock para completar la venta.");
      }
      throw ExceptionHandler.handleSQLException(LOGGER, e, "Error al procesar la venta.");
    } finally {
      closeResource(generatedKeys);
      closeResource(stmtInvoice);
      closeResource(stmtSale);
      closeResource(stmtStock);
      if (connection != null) {
        try {
          connection.setAutoCommit(true);
          connection.close();
        } catch (SQLException e) {
          LOGGER.error("Error closing connection", e);
        }
      }
    }
  }

  private void closeResource(AutoCloseable resource) {
    if (resource != null) {
      try {
        resource.close();
      } catch (Exception e) {
        LOGGER.error("Error closing resource", e);
      }
    }
  }
}