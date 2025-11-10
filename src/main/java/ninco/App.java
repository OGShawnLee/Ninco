package ninco;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ninco.business.dao.AccountDAO;
import ninco.common.ExceptionHandler;
import ninco.gui.AlertFacade;

public class App extends Application {
  private static final Logger LOGGER = LogManager.getLogger(App.class);
  private static final String APP_TITLE = "Ninco Store ERP System";
  // NOTE: Change the paths if the project is running in a different environment, i.e. NetBeans
  private static final String SIGN_UP_PAGE_FILE = "/GUISignUpPage.fxml";
  private static final String LOG_IN_PAGE_FILE = "/GUILogInPage.fxml";

  @Override
  public void start(Stage stage) {
    configureUncaughtErrorHandler();
    loadApplication(stage);
  }

  private void handleIllegalStateException(IllegalStateException e) {
    LOGGER.fatal("Error al iniciar la aplicación: {}", e.getMessage(), e);
    AlertFacade.showErrorAndWait(
      "Error de estado de interfaz gráfica al iniciar la aplicación. Por favor, comuníquese con el desarrollador si el error persiste."
    );
  }

  private void configureUncaughtErrorHandler() {
    Thread.currentThread().setUncaughtExceptionHandler((thread, e) -> Platform.runLater(() -> {
      AlertFacade.showErrorAndWait(
        ExceptionHandler.handleUnexpectedException(LOGGER, e, "Error de Aplicación.")
      );
    }));
  }

  private void loadApplication(Stage stage) {
    try {
      String initialPagePath = AccountDAO.getInstance().isThereAnAdminAccount() ? LOG_IN_PAGE_FILE : SIGN_UP_PAGE_FILE;
      FXMLLoader loader = new FXMLLoader(App.class.getResource(initialPagePath));
      Scene scene = new Scene(loader.load());

      stage.setTitle(APP_TITLE);
      stage.setScene(scene);
      stage.show();
    } catch (IOException e) {
      AlertFacade.showErrorAndWait(
        ExceptionHandler.handleGUILoadIOException(LOGGER, e)
      );
    } catch (IllegalStateException e) {
      handleIllegalStateException(e);
    } catch (Exception e) {
      AlertFacade.showErrorAndWait(
        ExceptionHandler.handleUnexpectedException(LOGGER, e, "Error desconocido al iniciar la aplicación.")
      );
    }
  }

  public static void main(String[] args) {
    launch(App.class);
  }
}
