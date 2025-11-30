package business.dao;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import ninco.business.dao.AccountDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;

import ninco.business.dto.AccountDTO;
import ninco.business.enumeration.Role;
import ninco.business.enumeration.State;
import ninco.common.UserDisplayableException;
import ninco.db.DBConnector;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountDAOTest {
    @Mock private DBConnector dbConnectorMock;
    @Mock private Connection connectionMock;
    @Mock private PreparedStatement statementMock;
    @Mock private ResultSet resultSetMock;

    private AccountDAO accountDAO;

    @BeforeEach
    void setUp() {
        accountDAO = AccountDAO.getInstance();
    }

    @Test
    void findOne_WhenUserExists_ShouldReturnAccountDTO() throws SQLException, Exception {
        String testEmail = "test@ninco.com";
        LocalDateTime now = LocalDateTime.now();

        try (MockedStatic<DBConnector> mockedDB = mockStatic(DBConnector.class)) {

            mockedDB.when(DBConnector::getInstance).thenReturn(dbConnectorMock);
            when(dbConnectorMock.getConnection()).thenReturn(connectionMock);

            when(connectionMock.prepareStatement(anyString())).thenReturn(statementMock);
            when(statementMock.executeQuery()).thenReturn(resultSetMock);

            when(resultSetMock.next()).thenReturn(true);
            when(resultSetMock.getInt("account_id")).thenReturn(1);
            when(resultSetMock.getString("email")).thenReturn(testEmail);
            when(resultSetMock.getString("password")).thenReturn("hashedPass");
            when(resultSetMock.getString("role")).thenReturn("ADMIN");
            when(resultSetMock.getString("state")).thenReturn("ACTIVE");
            when(resultSetMock.getTimestamp("created_at")).thenReturn(Timestamp.valueOf(now));

            AccountDTO result = accountDAO.findOne(testEmail);

            assertNotNull(result, "Result should not be null when user exists");
            assertEquals(testEmail, result.getEmail());
            assertEquals(Role.ADMIN, result.getRole());
            assertEquals(State.ACTIVE, result.getState());

            verify(resultSetMock).close();
            verify(statementMock).close();
            verify(connectionMock).close();
        }
    }

    @Test
    void findOne_WhenUserDoesNotExist_ShouldReturnNull() throws SQLException, Exception {
        try (MockedStatic<DBConnector> mockedDB = mockStatic(DBConnector.class)) {
            mockedDB.when(DBConnector::getInstance).thenReturn(dbConnectorMock);
            when(dbConnectorMock.getConnection()).thenReturn(connectionMock);
            when(connectionMock.prepareStatement(anyString())).thenReturn(statementMock);
            when(statementMock.executeQuery()).thenReturn(resultSetMock);

            when(resultSetMock.next()).thenReturn(false);

            AccountDTO result = accountDAO.findOne("ghost@ninco.com");

            assertNull(result, "Result should be null when no user is found");
        }
    }

    @Test
    void findOne_WhenSQLExceptionOccurs_ShouldThrowUserDisplayableException() throws SQLException, UserDisplayableException {
        try (MockedStatic<DBConnector> mockedDB = mockStatic(DBConnector.class)) {
            mockedDB.when(DBConnector::getInstance).thenReturn(dbConnectorMock);
            when(dbConnectorMock.getConnection()).thenReturn(connectionMock);

            SQLException sqlErrorConEstado = new SQLException("Error de conexión simulado", "S1000");

            when(connectionMock.prepareStatement(anyString())).thenThrow(sqlErrorConEstado);

            assertThrows(UserDisplayableException.class, () -> {
                accountDAO.findOne("error@ninco.com");
            });
        }
    }

    @Test
    void isThereAnAdminAccount_WhenAdminExists_ShouldReturnTrue() throws SQLException, Exception {
        try (MockedStatic<DBConnector> mockedDB = mockStatic(DBConnector.class)) {
            mockedDB.when(DBConnector::getInstance).thenReturn(dbConnectorMock);
            when(dbConnectorMock.getConnection()).thenReturn(connectionMock);
            when(connectionMock.prepareStatement(anyString())).thenReturn(statementMock);
            when(statementMock.executeQuery()).thenReturn(resultSetMock);

            when(resultSetMock.next()).thenReturn(true);
            when(resultSetMock.getInt(1)).thenReturn(1);

            boolean result = accountDAO.isThereAnAdminAccount();

            assertTrue(result, "Should return true if count > 0");
        }
    }

    @Test
    void isThereAnAdminAccount_WhenNoAdminExists_ShouldReturnFalse() throws SQLException, Exception {
        try (MockedStatic<DBConnector> mockedDB = mockStatic(DBConnector.class)) {
            mockedDB.when(DBConnector::getInstance).thenReturn(dbConnectorMock);
            when(dbConnectorMock.getConnection()).thenReturn(connectionMock);
            when(connectionMock.prepareStatement(anyString())).thenReturn(statementMock);
            when(statementMock.executeQuery()).thenReturn(resultSetMock);

            when(resultSetMock.next()).thenReturn(true);
            when(resultSetMock.getInt(1)).thenReturn(0);

            boolean result = accountDAO.isThereAnAdminAccount();

            assertFalse(result, "Should return false if count is 0");
        }
    }
}