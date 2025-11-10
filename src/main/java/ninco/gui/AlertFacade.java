package ninco.gui;

import javafx.scene.control.Alert;

import ninco.common.UserDisplayableException;

/**
 * AlertFacade is a utility class for displaying various types of alerts in the application.
 * It provides methods to show error, information, and success alerts with a consistent format.
 */
public class AlertFacade {
  private final String title;
  private final Alert.AlertType type;
  private final String header;
  private final String content;

  private AlertFacade(Alert.AlertType type, String title, String header, String content) {
    this.type = type;
    this.title = title;
    this.header = header;
    this.content = content;
  }

  public static void showErrorAndWait(UserDisplayableException e) {
    showErrorAndWait(e.getMessage());
  }

  public static void showErrorAndWait(String message) {
    new AlertFacade(
      Alert.AlertType.ERROR,
      "Error",
      "Error de Sistema",
      message
    ).showAndWait();
  }

  public static void showWarningAndWait(String message) {
    new AlertFacade(
      Alert.AlertType.WARNING,
      "Advertencia",
      "Advertencia del Sistema",
      message
    ).showAndWait();
  }

  public static void showInformationAndWait(String message) {
    new AlertFacade(
      Alert.AlertType.INFORMATION,
      "Información",
      "Información del Sistema",
      message
    ).showAndWait();
  }

  public static void showSuccessAndWait(String message) {
    new AlertFacade(
      Alert.AlertType.INFORMATION,
      "Éxito",
      "Operación Exitosa",
      message
    ).showAndWait();
  }

  public void showAndWait() {
    Alert alert = new Alert(type);
    alert.setTitle(title);
    alert.setHeaderText(header);
    alert.setContentText(content);
    alert.showAndWait();
  }
}