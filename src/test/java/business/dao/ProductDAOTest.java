package business.dao;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import ninco.business.dao.ProductDAO;
import ninco.business.dto.ProductDTO;
import ninco.db.DBConnector;

@ExtendWith(MockitoExtension.class)
class ProductDAOTest {

    @Mock private DBConnector dbConnectorMock;
    @Mock private Connection connectionMock;
    @Mock private CallableStatement statementMock;
    @Mock private ResultSet resultSetMock;

    private ProductDAO productDAO;

    @BeforeEach
    void setUp() {
        productDAO = ProductDAO.getInstance();
    }

    @Test
    void createOne_ShouldExecuteInsert() throws SQLException, Exception {
        ProductDTO productDTO = new ProductDTO("Laptop", "Gaming Laptop", "Dell", "1500.00");

        try (MockedStatic<DBConnector> mockedDB = mockStatic(DBConnector.class)) {
            mockedDB.when(DBConnector::getInstance).thenReturn(dbConnectorMock);
            when(dbConnectorMock.getConnection()).thenReturn(connectionMock);
            when(connectionMock.prepareCall(anyString())).thenReturn(statementMock);

            productDAO.createOne(productDTO);

            verify(statementMock).setString(1, "Laptop");
            verify(statementMock).setFloat(4, 1500.00f);
            verify(statementMock).executeUpdate();
        }
    }

    @Test
    void getAll_ShouldReturnProductList() throws SQLException, Exception {
        try (MockedStatic<DBConnector> mockedDB = mockStatic(DBConnector.class)) {
            mockedDB.when(DBConnector::getInstance).thenReturn(dbConnectorMock);
            when(dbConnectorMock.getConnection()).thenReturn(connectionMock);
            when(connectionMock.prepareStatement(anyString())).thenReturn(statementMock);
            when(statementMock.executeQuery()).thenReturn(resultSetMock);

            when(resultSetMock.next()).thenReturn(true).thenReturn(false);
            when(resultSetMock.getInt("product_id")).thenReturn(1);
            when(resultSetMock.getString("name")).thenReturn("Mouse");
            when(resultSetMock.getString("description")).thenReturn("Optical Mouse");
            when(resultSetMock.getString("brand")).thenReturn("Logitech");
            when(resultSetMock.getFloat("price")).thenReturn(20.0f);
            when(resultSetMock.getInt("stock")).thenReturn(100);
            when(resultSetMock.getTimestamp("created_at")).thenReturn(Timestamp.valueOf(LocalDateTime.now()));

            ArrayList<ProductDTO> result = productDAO.getAll();

            assertEquals(1, result.size());
            assertEquals("Mouse", result.get(0).getName());
        }
    }

    @Test
    void updateOne_WhenSuccessful_ShouldReturnFalse() throws SQLException, Exception {
        ProductDTO original = new ProductDTO(1, "Old", "Old Desc", "Old Brand", 10.0f, 5, LocalDateTime.now());
        ProductDTO updated = new ProductDTO(1, "New", "New Desc", "New Brand", 15.0f, 5, LocalDateTime.now());

        try (MockedStatic<DBConnector> mockedDB = mockStatic(DBConnector.class)) {
            mockedDB.when(DBConnector::getInstance).thenReturn(dbConnectorMock);
            when(dbConnectorMock.getConnection()).thenReturn(connectionMock);
            when(connectionMock.prepareCall(anyString())).thenReturn(statementMock);
            when(statementMock.getUpdateCount()).thenReturn(1);

            boolean failed = productDAO.updateOne(updated, original);

            assertFalse(failed);
            verify(statementMock).executeUpdate();
        }
    }
}