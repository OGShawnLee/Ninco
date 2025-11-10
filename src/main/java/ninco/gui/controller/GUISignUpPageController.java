package ninco.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import ninco.business.dao.AccountDAO;
import ninco.business.dao.EmployeeDAO;
import ninco.business.dto.AccountDTO;
import ninco.business.dto.EmployeeDTO;
import ninco.business.enumeration.Role;
import ninco.business.enumeration.State;
import ninco.business.rules.ValidationResult;
import ninco.business.rules.Validator;
import ninco.common.InvalidFieldException;
import ninco.common.UserDisplayableException;
import ninco.gui.AlertFacade;

import java.util.Objects;

public class GUISignUpPageController extends Controller {
  @FXML
  private Label labelTagEmail;
  @FXML
  private TextField fieldEmail;
  @FXML
  private Label labelTagPassword;
  @FXML
  private PasswordField fieldPassword;
  @FXML
  private Label labelTagConfirmPassword;
  @FXML
  private PasswordField fieldConfirmPassword;
  @FXML
  private TextField fieldName;
  @FXML
  private Label labelTagName;
  @FXML
  private TextField fieldLastName;
  @FXML
  private Label labelTagLastName;

  public void initialize() {
    cleanErrorLabels();
    showInformation();
  }

  public void showInformation() {
    AlertFacade.showInformationAndWait("Welcome to NincoERP Store!");
    AlertFacade.showInformationAndWait("Create your Admin account to start using the system.");
  }

  public void cleanErrorLabels() {
    labelTagEmail.setText("");
    labelTagPassword.setText("");
    labelTagConfirmPassword.setText("");
    labelTagName.setText("");
    labelTagLastName.setText("");
  }

  public boolean isValidData() {
    boolean isValid = true;

    ValidationResult result = Validator.getIsInvalidEmailResult(fieldEmail.getText());
    if (result.isInvalid()) {
      labelTagEmail.setText(result.getMessage());
      isValid = false;
    }

    ValidationResult fieldPasswordResult = Validator.getIsInvalidPasswordResult(fieldPassword.getText());
    if (fieldPasswordResult.isInvalid()) {
      labelTagPassword.setText(fieldPasswordResult.getMessage());
      isValid = false;
    }

    ValidationResult fieldConfirmPasswordResult = Validator.getIsInvalidPasswordResult(fieldConfirmPassword.getText());
    if (fieldConfirmPasswordResult.isInvalid()) {
      labelTagConfirmPassword.setText(fieldConfirmPasswordResult.getMessage());
      isValid = false;
    }

    if (!fieldPasswordResult.isInvalid() && !fieldConfirmPasswordResult.isInvalid()) {
      if (!Objects.equals(fieldPassword.getText(), fieldConfirmPassword.getText())) {
        labelTagConfirmPassword.setText("Passwords do not match.");
        isValid = false;
      }
    }

    result = Validator.getIsInvalidNameResult(fieldName.getText(), "Name", 3, 64);
    if (result.isInvalid()) {
      labelTagName.setText(result.getMessage());
      isValid = false;
    }

    result = Validator.getIsInvalidNameResult(fieldLastName.getText(), "Last Name", 3, 64);
    if (result.isInvalid()) {
      labelTagLastName.setText(result.getMessage());
      isValid = false;
    }

    return isValid;
  }

  public AccountDTO getAccountDTOFromInput() throws InvalidFieldException {
    return new AccountDTO(
      fieldEmail.getText(),
      fieldPassword.getText(),
      Role.ADMIN,
      State.ACTIVE
    );
  }

  public EmployeeDTO getEmployeeDTOFromInput() throws InvalidFieldException {
    return new EmployeeDTO(
      -1,
      fieldEmail.getText(),
      Role.ADMIN,
      State.ACTIVE,
      fieldName.getText(),
      fieldLastName.getText()
    );
  }

  public void onClickSignUp() {
    try {
      cleanErrorLabels();
      if (isValidData()) {
        AccountDTO accountDTO = getAccountDTOFromInput();
        AccountDTO existingAccountDTO = AccountDAO.getInstance().findOne(accountDTO.getEmail());

        if (existingAccountDTO != null) {
          AlertFacade.showErrorAndWait("Unable to sign up. An account with this email already exists.");
        }

        EmployeeDAO.getInstance().createOne(
          getEmployeeDTOFromInput(),
          accountDTO.getPassword()
        );
        AlertFacade.showSuccessAndWait("Admin account created successfully!");
        AlertFacade.showSuccessAndWait("Please log in with your new credentials.");
        navigateFromThisPageTo("Log In Page", "GUILogInPage");
      }
    } catch (InvalidFieldException | UserDisplayableException e) {
      AlertFacade.showErrorAndWait(e.getMessage());
    }
  }
}
