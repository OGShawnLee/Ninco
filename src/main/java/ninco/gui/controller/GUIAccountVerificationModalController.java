package ninco.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import ninco.business.dao.EmployeeDAO;
import ninco.business.dao.PendingRegistrationsDAO;
import ninco.business.dto.SignUpContext;
import ninco.business.rules.Validator;
import ninco.common.UserDisplayableException;
import ninco.gui.AlertFacade;

public class GUIAccountVerificationModalController extends Controller implements ContextController<SignUpContext> {
  @FXML
  private TextField fieldPin;
  @FXML
  private Label labelTagPin;
  private SignUpContext context;

  @Override
  public void setContext(SignUpContext data) {
    this.context = data;
  }

  public void initialize() {
    cleanErrorLabels();
  }

  private void cleanErrorLabels() {
    labelTagPin.setText("");
  }

  private boolean isInvalidData() {
    boolean isInvalidData = false;

    if (Validator.isInvalidString(fieldPin.getText())) {
      labelTagPin.setText("Por favor introduzca el código.");
      isInvalidData = true;
    }

    return isInvalidData;
  }

  private void registerAccount() throws UserDisplayableException {
    EmployeeDAO.getInstance().createOne(context.getEmployeeDTO(), context.getRawPassword());
    AlertFacade.showSuccessAndWait("¡Cuenta verificada y creada exitosamente!");
    close();
  }

  public void onClickVerify() {
    if (isInvalidData()) return;

    try {
      boolean isValid = PendingRegistrationsDAO.getInstance().verifyPin(
        context.getEmployeeDTO().getEmail(),
        fieldPin.getText().trim()
      );

      if (isValid) {
        registerAccount();
      }
    } catch (UserDisplayableException e) {
      AlertFacade.showErrorAndWait(e.getMessage());
    }
  }
}