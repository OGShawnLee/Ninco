package ninco.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import ninco.common.ExceptionHandler;
import ninco.common.UserDisplayableException;

/**
 * <b>DBConnector</b> is a singleton class responsible for managing the database connection.
 * It reads the database configuration from a properties file and provides a method
 * to obtain a connection to the database.
 */
public class DBConnector {
  private static final Logger LOGGER = LogManager.getLogger(DBConnector.class);
  private static DBConnector instance;
  private final String URL;
  private final String USERNAME;
  private final String PASSWORD;
  // NOTE: Change the path if the project is running in a different environment
  private final String DB_PROPERTIES_FILE = "src/main/resources/db.properties";

  /**
   * Private constructor to initialize the DBConnector instance.
   * Loads database configuration from the properties file.
   * @throws UserDisplayableException if an error occurs during initialization
   */
  private DBConnector() throws UserDisplayableException {
    Properties properties = new Properties();

    try (FileInputStream input = new FileInputStream(DB_PROPERTIES_FILE)) {
      properties.load(input);

      this.URL = properties.getProperty("db.url");
      this.USERNAME = properties.getProperty("db.username");
      this.PASSWORD = properties.getProperty("db.password");

      handlePropertiesVerification();
    } catch (FileNotFoundException e) {
      throw handleConfigurationFileNotFound(e);
    } catch (IOException e) {
      throw getUserDisplayableExceptionFromDBInitIOException(e);
    }
  }

  /**
   * Converts an IOException during DB initialization into a UserDisplayableException.
   * @param e the IOException to handle
   * @return UserDisplayableException with a user-friendly message
   */
  private UserDisplayableException getUserDisplayableExceptionFromDBInitIOException(IOException e) {
    return ExceptionHandler.handleIOException(LOGGER, e, "No ha sido posible cargar la configuración de la base de datos.");
  }

  /**
   * Handles the case when the database configuration file is not found.
   * @param e the FileNotFoundException to handle
   * @return UserDisplayableException with a user-friendly message
   */
  private UserDisplayableException handleConfigurationFileNotFound(FileNotFoundException e) {
    LOGGER.fatal("No se ha encontrado el archivo de configuración de la base de datos: db.properties", e);
    return new UserDisplayableException(
      "No se ha encontrado el archivo de configuración de la base de datos. Por favor, comuníquese con el desarrollador del sistema."
    );
  }

  /**
   * Verifies that the database connection properties are properly set.
   * @throws UserDisplayableException if any property is missing
   */
  private void handlePropertiesVerification() throws UserDisplayableException {
    if (URL == null || USERNAME == null || PASSWORD == null) {
      LOGGER.fatal("Las propiedades de conexión a la base de datos no están configuradas correctamente. Revisar db.properties.");
      throw new UserDisplayableException(
        "Las propiedades de conexión a la base de datos no están configuradas correctamente. Por favor, comuníquese con el desarrollador del sistema."
      );
    }
  }

  /**
   * Returns the singleton instance of DBConnector.
   * @return DBConnector instance
   * @throws UserDisplayableException if an error occurs during initialization
   */
  public static synchronized DBConnector getInstance() throws UserDisplayableException {
    if (instance == null) {
      instance = new DBConnector();
    }

    return instance;
  }

  /**
   * Establishes and returns a connection to the database.
   * @return Connection to the database
   * @throws UserDisplayableException if a database access error occurs
   */
  public Connection getConnection() throws UserDisplayableException {
    try {
      return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e);
    }
  }
}