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

import ninco.common.InvalidFieldException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import ninco.business.dao.EmployeeDAO;
import ninco.business.dto.EmployeeDTO;
import ninco.business.enumeration.Role;
import ninco.business.enumeration.State;
import ninco.common.UserDisplayableException;
import ninco.db.DBConnector;

@ExtendWith(MockitoExtension.class)
class EmployeeDAOTest {

    @Mock private DBConnector dbConnectorMock;
    @Mock private Connection connectionMock;
    @Mock private CallableStatement statementMock;
    @Mock private ResultSet resultSetMock;

    private EmployeeDAO employeeDAO;

    @BeforeEach
    void setUp() {
        employeeDAO = EmployeeDAO.getInstance();
    }

    @Test
    void createOne_ShouldCommitTransaction() throws SQLException, Exception {
        EmployeeDTO employeeDTO = new EmployeeDTO(1, "test@ninco.com", Role.CASHIER, State.ACTIVE, "John", "Doe");
        String password = "Password123!";

        try (MockedStatic<DBConnector> mockedDB = mockStatic(DBConnector.class)) {
            mockedDB.when(DBConnector::getInstance).thenReturn(dbConnectorMock);
            when(dbConnectorMock.getConnection()).thenReturn(connectionMock);
            when(connectionMock.prepareCall(anyString())).thenReturn(statementMock);

            employeeDAO.createOne(employeeDTO, password);

            verify(connectionMock).setAutoCommit(false);
            verify(statementMock, times(2)).executeUpdate();
            verify(connectionMock).commit();
            verify(statementMock, times(2)).close();
            verify(connectionMock).close();
        }
    }

    @Test
    void findOne_WhenExists_ShouldReturnEmployeeDTO() throws SQLException, Exception {
        String email = "test@ninco.com";
        LocalDateTime now = LocalDateTime.now();

        try (MockedStatic<DBConnector> mockedDB = mockStatic(DBConnector.class)) {
            mockedDB.when(DBConnector::getInstance).thenReturn(dbConnectorMock);
            when(dbConnectorMock.getConnection()).thenReturn(connectionMock);
            when(connectionMock.prepareStatement(anyString())).thenReturn(statementMock);
            when(statementMock.executeQuery()).thenReturn(resultSetMock);

            when(resultSetMock.next()).thenReturn(true);
            when(resultSetMock.getInt("employee_id")).thenReturn(1);
            when(resultSetMock.getInt("account_id")).thenReturn(10);
            when(resultSetMock.getInt("store_id")).thenReturn(5);
            when(resultSetMock.getString("store_name")).thenReturn("Main Store");
            when(resultSetMock.getString("email")).thenReturn(email);
            when(resultSetMock.getString("role")).thenReturn("ADMIN");
            when(resultSetMock.getString("state")).thenReturn("ACTIVE");
            when(resultSetMock.getString("name")).thenReturn("Jane");
            when(resultSetMock.getString("last_name")).thenReturn("Doe");
            when(resultSetMock.getTimestamp("created_at")).thenReturn(Timestamp.valueOf(now));

            EmployeeDTO result = employeeDAO.findOne(email);

            assertNotNull(result);
            assertEquals(email, result.getEmail());
            assertEquals("Jane", result.getName());
            assertEquals(Role.ADMIN, result.getRole());
        }
    }

    @Test
    void getAll_ShouldReturnList() throws SQLException, Exception {
        try (MockedStatic<DBConnector> mockedDB = mockStatic(DBConnector.class)) {
            mockedDB.when(DBConnector::getInstance).thenReturn(dbConnectorMock);
            when(dbConnectorMock.getConnection()).thenReturn(connectionMock);
            when(connectionMock.prepareStatement(anyString())).thenReturn(statementMock);
            when(statementMock.executeQuery()).thenReturn(resultSetMock);

            when(resultSetMock.next()).thenReturn(true).thenReturn(true).thenReturn(false);
            when(resultSetMock.getString("role")).thenReturn("CASHIER");
            when(resultSetMock.getString("state")).thenReturn("ACTIVE");
            when(resultSetMock.getTimestamp("created_at")).thenReturn(Timestamp.valueOf(LocalDateTime.now()));
            when(resultSetMock.getString("email")).thenReturn("user@test.com");
            when(resultSetMock.getString("name")).thenReturn("User");
            when(resultSetMock.getString("last_name")).thenReturn("Test");
            when(resultSetMock.getString("store_name")).thenReturn("Main Store");

            ArrayList<EmployeeDTO> result = employeeDAO.getAll();

            assertEquals(2, result.size());
        }
    }

    @Test
    void updateOne_ShouldUpdateEmployeeAndAccount() throws SQLException, Exception {
        EmployeeDTO employeeDTO = new EmployeeDTO(1, 10, 5, "Store", "test@ninco.com", Role.ADMIN, State.INACTIVE, "NewName", "NewLast", LocalDateTime.now());

        try (MockedStatic<DBConnector> mockedDB = mockStatic(DBConnector.class)) {
            mockedDB.when(DBConnector::getInstance).thenReturn(dbConnectorMock);
            when(dbConnectorMock.getConnection()).thenReturn(connectionMock);
            when(connectionMock.prepareStatement(anyString())).thenReturn(statementMock);

            employeeDAO.updateOne(employeeDTO);

            verify(connectionMock).setAutoCommit(false);
            verify(statementMock, times(2)).executeUpdate();
            verify(connectionMock).commit();
        }
    }
}