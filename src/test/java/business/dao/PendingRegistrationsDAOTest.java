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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import ninco.business.dao.PendingRegistrationsDAO;
import ninco.business.dto.PendingRegistrationDTO;
import ninco.business.enumeration.Role;
import ninco.common.UserDisplayableException;
import ninco.db.DBConnector;

@ExtendWith(MockitoExtension.class)
class PendingRegistrationsDAOTest {

    @Mock private DBConnector dbConnectorMock;
    @Mock private Connection connectionMock;
    @Mock private PreparedStatement statementMock;
    @Mock private ResultSet resultSetMock;
    @Mock private PendingRegistrationDTO dtoMock;

    private PendingRegistrationsDAO dao;

    @BeforeEach
    void setUp() {
        dao = PendingRegistrationsDAO.getInstance();
    }

    @Test
    void save_ShouldExecuteUpsert() throws SQLException, Exception {
        LocalDateTime now = LocalDateTime.now();
        String email = "test@ninco.com";
        String pin = "123456";
        String password = "hashedPassword";

        when(dtoMock.getEmail()).thenReturn(email);
        when(dtoMock.getPin()).thenReturn(pin);
        when(dtoMock.getExpiresAt()).thenReturn(now);
        when(dtoMock.getPassword()).thenReturn(password);

        doReturn(Role.ADMIN).when(dtoMock).getRole();

        try (MockedStatic<DBConnector> mockedDB = mockStatic(DBConnector.class)) {
            mockedDB.when(DBConnector::getInstance).thenReturn(dbConnectorMock);
            when(dbConnectorMock.getConnection()).thenReturn(connectionMock);
            when(connectionMock.prepareStatement(anyString())).thenReturn(statementMock);

            dao.save(dtoMock);

            verify(statementMock).setString(1, email);
            verify(statementMock).setString(2, pin);
            verify(statementMock).setTimestamp(3, Timestamp.valueOf(now));
            verify(statementMock).setString(4, password);
            verify(statementMock).setString(5, "ADMIN");
            verify(statementMock).executeUpdate();
            verify(statementMock).close();
            verify(connectionMock).close();
        }
    }

    @Test
    void save_WhenSQLExceptionOccurs_ShouldThrowUserDisplayableException() throws SQLException {
        try (MockedStatic<DBConnector> mockedDB = mockStatic(DBConnector.class)) {
            mockedDB.when(DBConnector::getInstance).thenReturn(dbConnectorMock);
            when(dbConnectorMock.getConnection()).thenReturn(connectionMock);

            SQLException sqlException = new SQLException("DB Error", "S1000");
            when(connectionMock.prepareStatement(anyString())).thenThrow(sqlException);

            assertThrows(UserDisplayableException.class, () -> dao.save(dtoMock));
        } catch (UserDisplayableException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void verifyPin_WhenMatchFound_ShouldReturnTrue() throws SQLException, Exception {
        String email = "test@ninco.com";
        String pin = "123456";

        try (MockedStatic<DBConnector> mockedDB = mockStatic(DBConnector.class)) {
            mockedDB.when(DBConnector::getInstance).thenReturn(dbConnectorMock);
            when(dbConnectorMock.getConnection()).thenReturn(connectionMock);
            when(connectionMock.prepareStatement(anyString())).thenReturn(statementMock);
            when(statementMock.executeQuery()).thenReturn(resultSetMock);

            when(resultSetMock.next()).thenReturn(true);
            when(resultSetMock.getInt(1)).thenReturn(1);

            boolean result = dao.verifyPin(email, pin);

            assertTrue(result);
            verify(statementMock).setString(1, email);
            verify(statementMock).setString(2, pin);
            verify(resultSetMock).close();
            verify(statementMock).close();
            verify(connectionMock).close();
        }
    }

    @Test
    void verifyPin_WhenNoMatch_ShouldReturnFalse() throws SQLException, Exception {
        String email = "test@ninco.com";
        String pin = "000000";

        try (MockedStatic<DBConnector> mockedDB = mockStatic(DBConnector.class)) {
            mockedDB.when(DBConnector::getInstance).thenReturn(dbConnectorMock);
            when(dbConnectorMock.getConnection()).thenReturn(connectionMock);
            when(connectionMock.prepareStatement(anyString())).thenReturn(statementMock);
            when(statementMock.executeQuery()).thenReturn(resultSetMock);

            when(resultSetMock.next()).thenReturn(true);
            when(resultSetMock.getInt(1)).thenReturn(0);

            boolean result = dao.verifyPin(email, pin);

            assertFalse(result);
        }
    }

    @Test
    void verifyPin_WhenSQLExceptionOccurs_ShouldThrowUserDisplayableException() throws SQLException {
        try (MockedStatic<DBConnector> mockedDB = mockStatic(DBConnector.class)) {
            mockedDB.when(DBConnector::getInstance).thenReturn(dbConnectorMock);
            when(dbConnectorMock.getConnection()).thenReturn(connectionMock);

            SQLException sqlException = new SQLException("DB Error", "S1000");
            when(connectionMock.prepareStatement(anyString())).thenThrow(sqlException);

            assertThrows(UserDisplayableException.class, () -> dao.verifyPin("email", "pin"));
        } catch (UserDisplayableException e) {
            throw new RuntimeException(e);
        }
    }
}