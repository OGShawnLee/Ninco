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

import ninco.business.dao.StockDAO;
import ninco.business.dto.StockDTO;
import ninco.business.dto.StoreDTO;
import ninco.common.UserDisplayableException;
import ninco.db.DBConnector;

@ExtendWith(MockitoExtension.class)
class StockDAOTest {

    @Mock private DBConnector dbConnectorMock;
    @Mock private Connection connectionMock;
    @Mock private CallableStatement statementMock;
    @Mock private ResultSet resultSetMock;

    private StockDAO stockDAO;

    @BeforeEach
    void setUp() {
        stockDAO = StockDAO.getInstance();
    }

    @Test
    void createOne_ShouldExecuteInsert() throws SQLException, Exception {
        StockDTO stockDTO = new StockDTO(1, 1, "50");

        try (MockedStatic<DBConnector> mockedDB = mockStatic(DBConnector.class)) {
            mockedDB.when(DBConnector::getInstance).thenReturn(dbConnectorMock);
            when(dbConnectorMock.getConnection()).thenReturn(connectionMock);
            when(connectionMock.prepareStatement(anyString())).thenReturn(statementMock);

            stockDAO.createOne(stockDTO);

            verify(statementMock).setInt(1, 1);
            verify(statementMock).setInt(2, 1);
            verify(statementMock).setInt(3, 50);
            verify(statementMock).executeUpdate();
        }
    }

    @Test
    void createOne_WhenSQLExceptionOccurs_ShouldThrowUserDisplayableException() throws SQLException, Exception {
        StockDTO stockDTO = new StockDTO(1, 1, "50");

        try (MockedStatic<DBConnector> mockedDB = mockStatic(DBConnector.class)) {
            mockedDB.when(DBConnector::getInstance).thenReturn(dbConnectorMock);
            when(dbConnectorMock.getConnection()).thenReturn(connectionMock);

            SQLException sqlException = new SQLException("DB Error", "S1000");
            when(connectionMock.prepareStatement(anyString())).thenThrow(sqlException);

            assertThrows(UserDisplayableException.class, () -> {
                stockDAO.createOne(stockDTO);
            });
        }
    }

    @Test
    void getAll_ShouldReturnList() throws SQLException, Exception {
        try (MockedStatic<DBConnector> mockedDB = mockStatic(DBConnector.class)) {
            mockedDB.when(DBConnector::getInstance).thenReturn(dbConnectorMock);
            when(dbConnectorMock.getConnection()).thenReturn(connectionMock);
            when(connectionMock.prepareStatement(anyString())).thenReturn(statementMock);
            when(statementMock.executeQuery()).thenReturn(resultSetMock);

            when(resultSetMock.next()).thenReturn(true).thenReturn(false);
            when(resultSetMock.getInt("product_id")).thenReturn(1);
            when(resultSetMock.getInt("store_id")).thenReturn(1);
            when(resultSetMock.getString("product_name")).thenReturn("Product A");
            when(resultSetMock.getString("store_name")).thenReturn("Store A");
            when(resultSetMock.getInt("quantity")).thenReturn(100);
            when(resultSetMock.getFloat("price")).thenReturn(10.0f);
            when(resultSetMock.getTimestamp("created_at")).thenReturn(Timestamp.valueOf(LocalDateTime.now()));

            ArrayList<StockDTO> result = stockDAO.getAll();

            assertEquals(1, result.size());
            assertEquals("Product A", result.get(0).getProductName());
            assertEquals(100, result.get(0).getQuantity());
        }
    }

    @Test
    void getAllByStore_ShouldReturnListForSpecificStore() throws SQLException, Exception {
        StoreDTO storeDTO = new StoreDTO(1, "Store A", "123 Main Street", "5551234567", LocalDateTime.now(), 5);

        try (MockedStatic<DBConnector> mockedDB = mockStatic(DBConnector.class)) {
            mockedDB.when(DBConnector::getInstance).thenReturn(dbConnectorMock);
            when(dbConnectorMock.getConnection()).thenReturn(connectionMock);
            when(connectionMock.prepareStatement(anyString())).thenReturn(statementMock);
            when(statementMock.executeQuery()).thenReturn(resultSetMock);

            when(resultSetMock.next()).thenReturn(true).thenReturn(false);
            when(resultSetMock.getInt("product_id")).thenReturn(1);
            when(resultSetMock.getInt("store_id")).thenReturn(1);
            when(resultSetMock.getString("product_name")).thenReturn("Product A");
            when(resultSetMock.getString("store_name")).thenReturn("Store A");
            when(resultSetMock.getInt("quantity")).thenReturn(50);
            when(resultSetMock.getFloat("price")).thenReturn(10.0f);
            when(resultSetMock.getTimestamp("created_at")).thenReturn(Timestamp.valueOf(LocalDateTime.now()));

            ArrayList<StockDTO> result = stockDAO.getAllByStore(storeDTO);

            verify(statementMock).setInt(1, storeDTO.getID());
            assertEquals(1, result.size());
            assertEquals(50, result.get(0).getQuantity());
        }
    }
}