package ninco.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import ninco.business.AuthClient;
import ninco.business.dao.AccountDAO;
import ninco.business.dao.EmployeeDAO;
import ninco.business.dto.AccountDTO;
import ninco.business.dto.EmployeeDTO;
import ninco.business.rules.ValidationResult;
import ninco.business.rules.Validator;
import ninco.common.InvalidFieldException;
import ninco.common.UserDisplayableException;
import ninco.gui.AlertFacade;

public class GUILogInPageController extends Controller {
  @FXML
  private Label labelTagEmail;
  @FXML
  private TextField fieldEmail;
  @FXML
  private Label labelTagPassword;
  @FXML
  private PasswordField fieldPassword;

  public void initialize() {
    cleanErrorLabels();
  }

  public void cleanErrorLabels() {
    labelTagEmail.setText("");
    labelTagPassword.setText("");
  }

  public boolean isValidData() {
    boolean isValid = true;

    ValidationResult result = Validator.getIsInvalidEmailResult(fieldEmail.getText());
    if (result.isInvalid()) {
      labelTagEmail.setText(result.getMessage());
      isValid = false;
    }

    result = Validator.getIsInvalidPasswordResult(fieldPassword.getText());
    if (result.isInvalid()) {
      labelTagPassword.setText(result.getMessage());
      isValid = false;
    }

    return isValid;
  }

  public void onClickLogIn() {
    try {
      cleanErrorLabels();
      if (isValidData()) {
        String password = fieldPassword.getText();
        String email = fieldEmail.getText();
        AccountDTO existingAccountDTO = AccountDAO.getInstance().findOne(email);

        if (existingAccountDTO == null) {
          AlertFacade.showErrorAndWait("Unable to log in. Invalid credentials.");
        }

        if (existingAccountDTO.hasPasswordMatch(password)) {
          EmployeeDTO employeeDTO = EmployeeDAO.getInstance().findOne(email);

          if (employeeDTO == null) {
            AlertFacade.showErrorAndWait("Unable to log in. Invalid credentials.");
            return;
          }

          AuthClient.getInstance().setCurrentUser(employeeDTO);
          navigateToLandingPage();
        } else {
          AlertFacade.showErrorAndWait("Unable to log in. Invalid credentials.");
        }
      }
    } catch (InvalidFieldException | UserDisplayableException e) {
      AlertFacade.showErrorAndWait(e.getMessage());
    }
  }
}
