package ninco.gui.modal;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;


/**
 * Modal class that represents a modal dialog in the application.
 * It is responsible for loading the FXML file, creating the stage, and managing the modal's lifecycle
 * <p>
 * This class is used to create and display modals with a specific configuration such as
 * title, resource file name, and optional close actions.
 * <p>
 * The files used for this have the name convention of ending with "Modal"
 * (e.g., "RegisterAcademicModal", "ManageAcademicModal", etc.).
 */
class Modal {
  private final ModalFacadeConfiguration configuration;
  private final Parent parent;
  private final Scene scene;
  private final Stage stage;
  private final FXMLLoader loader;

  public Modal(ModalFacadeConfiguration configuration) throws IOException {
    this.configuration = configuration;
    this.loader = createFXMLLoader(configuration.getResourceFileName());
    this.parent = loader.load();
    this.scene = new Scene(this.parent);
    this.stage = createStage(configuration.getTitle());
  }

  private ModalFacadeConfiguration getConfiguration() {
    return configuration;
  }

  public Parent getParent() {
    return parent;
  }

  public Scene getScene() {
    return scene;
  }

  public Stage getStage() {
    return stage;
  }

  private FXMLLoader createFXMLLoader(String resourceFileName) throws IOException {
    return new FXMLLoader(
      Objects.requireNonNull(Modal.class.getResource("/" + resourceFileName + ".fxml"))
    );
  }

  private Stage createStage(String title) {
    Stage modalStage = new Stage();
    modalStage.setTitle(title);
    modalStage.setScene(scene);
    modalStage.setResizable(false);
    modalStage.initModality(Modality.APPLICATION_MODAL);

    getConfiguration().getOnClose().ifPresent(runnable ->
      modalStage.setOnHidden(event -> runnable.run())
    );

    return modalStage;
  }

  public <T> T getController() {
    return loader.getController();
  }

  public void showAndWait() {
    stage.showAndWait();
  }
}