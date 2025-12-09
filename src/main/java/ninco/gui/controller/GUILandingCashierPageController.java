package ninco.gui.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import ninco.App;
import ninco.gui.AlertFacade;
import java.io.IOException;
import java.util.Optional;

public class GUILandingCashierPageController extends LandingController {

  public void onClickSearchProduct() {
    TextInputDialog dialog = new TextInputDialog();
    dialog.setTitle("New Sale");
    dialog.setHeaderText("Start a New Sale");
    dialog.setContentText("Please enter the client's name:");

    Optional<String> result = dialog.showAndWait();

    result.ifPresent(name -> {
      if (!name.trim().isEmpty()) {
        navigateToSalePage(name);
      } else {
        AlertFacade.showWarningAndWait("Client name cannot be empty.");
      }
    });
  }

  public void onClickStartNewSale() {
    Stage stage = getStageSafe();
    if (stage != null) {
      GUIReviewStockListPageController.navigateToStockListPage(stage);
    }
  }

  private void navigateToSalePage(String clientName) {
    if (ninco.business.AuthClient.getInstance().getCurrentUser() == null) {
      AlertFacade.showErrorAndWait("Error de Sesión: No hay usuario logueado.");
      return;
    }

    if (container == null) {
      AlertFacade.showErrorAndWait("ERROR CRÍTICO: El campo 'container' es NULL.\n" +
              "Por favor verifica src/main/resources/GUILandingCashierPage.fxml\n" +
              "El AnchorPane raíz debe tener: fx:id=\"container\"");
      return;
    }

    try {
      FXMLLoader loader = new FXMLLoader(App.class.getResource("/GUISalePage.fxml"));
      Parent root = loader.load();

      GUISalePageController controller = loader.getController();
      if (controller == null) {
        AlertFacade.showErrorAndWait("Error: Controlador no encontrado para GUISalePage.fxml");
        return;
      }

      controller.setContext(clientName);

      Scene scene = new Scene(root);

      Stage stage = (Stage) container.getScene().getWindow();
      stage.setScene(scene);
      stage.show();

    } catch (IOException e) {
      AlertFacade.showErrorAndWait("Error loading Sale Page: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private Stage getStageSafe() {
    if (container != null && container.getScene() != null) {
      return (Stage) container.getScene().getWindow();
    }
    AlertFacade.showErrorAndWait("Error UI: No se pudo obtener la ventana.");
    return null;
  }
}