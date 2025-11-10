package ninco.gui.controller;

import ninco.business.AuthClient;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public abstract class LandingController extends Controller {
  @FXML
  private Label labelEmail;

  public void initialize() {
    labelEmail.setText(AuthClient.getInstance().getCurrentUser().getEmail());
  }

  public void onClickManageProfile() {
    // TODO: Add Update Profile
  }

  public void onClickLogOut() {
    AuthClient.getInstance().setCurrentUser(null);
    navigateFromThisPageTo("Página de Inicio", "GUILoginPage");
  }
}
