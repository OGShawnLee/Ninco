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

import ninco.common.InvalidFieldException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import ninco.business.dao.StoreDAO;
import ninco.business.dto.StoreDTO;
import ninco.common.UserDisplayableException;
import ninco.db.DBConnector;

@ExtendWith(MockitoExtension.class)
class StoreDAOTest {

    @Mock private DBConnector dbConnectorMock;
    @Mock private Connection connectionMock;
    @Mock private CallableStatement statementMock;
    @Mock private ResultSet resultSetMock;

    private StoreDAO storeDAO;

    @BeforeEach
    void setUp() {
        storeDAO = StoreDAO.getInstance();
    }

    @Test
    void createOne_ShouldExecuteInsert() throws SQLException, Exception {
        StoreDTO storeDTO = new StoreDTO("Store 1", "123 Main Street", "5551234567");

        try (MockedStatic<DBConnector> mockedDB = mockStatic(DBConnector.class)) {
            mockedDB.when(DBConnector::getInstance).thenReturn(dbConnectorMock);
            when(dbConnectorMock.getConnection()).thenReturn(connectionMock);
            when(connectionMock.prepareCall(anyString())).thenReturn(statementMock);

            storeDAO.createOne(storeDTO);

            verify(statementMock).setString(1, "Store 1");
            verify(statementMock).executeUpdate();
        }
    }

    @Test
    void findOneByPhoneNumber_WhenExists_ShouldReturnStore() throws SQLException, Exception {
        String phone = "5551234567";
        String validAddress = "123 Main Street Avenue";

        try (MockedStatic<DBConnector> mockedDB = mockStatic(DBConnector.class)) {
            mockedDB.when(DBConnector::getInstance).thenReturn(dbConnectorMock);
            when(dbConnectorMock.getConnection()).thenReturn(connectionMock);
            when(connectionMock.prepareStatement(anyString())).thenReturn(statementMock);
            when(statementMock.executeQuery()).thenReturn(resultSetMock);

            when(resultSetMock.next()).thenReturn(true);
            when(resultSetMock.getInt("store_id")).thenReturn(1);
            when(resultSetMock.getString("name")).thenReturn("Store 1");
            when(resultSetMock.getString("address")).thenReturn(validAddress);
            when(resultSetMock.getString("phone")).thenReturn(phone);
            when(resultSetMock.getInt("employee_count")).thenReturn(5);
            when(resultSetMock.getTimestamp("created_at")).thenReturn(Timestamp.valueOf(LocalDateTime.now()));

            StoreDTO result = storeDAO.findOneByPhoneNumber(phone);

            assertNotNull(result);
            assertEquals(phone, result.getPhoneNumber());
            assertEquals(validAddress, result.getAddress());
        }
    }

    @Test
    void getOne_WhenNotExists_ShouldThrowException() throws SQLException, Exception {
        int id = 99;

        try (MockedStatic<DBConnector> mockedDB = mockStatic(DBConnector.class)) {
            mockedDB.when(DBConnector::getInstance).thenReturn(dbConnectorMock);
            when(dbConnectorMock.getConnection()).thenReturn(connectionMock);
            when(connectionMock.prepareStatement(anyString())).thenReturn(statementMock);
            when(statementMock.executeQuery()).thenReturn(resultSetMock);

            when(resultSetMock.next()).thenReturn(false);

            assertThrows(UserDisplayableException.class, () -> storeDAO.getOne(id));
        }
    }
}