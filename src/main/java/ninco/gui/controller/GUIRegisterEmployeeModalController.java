package ninco.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import ninco.business.dao.AccountDAO;
import ninco.business.dao.EmployeeDAO;
import ninco.business.dao.StoreDAO;
import ninco.business.dto.AccountDTO;
import ninco.business.dto.EmployeeDTO;
import ninco.business.dto.StoreDTO;
import ninco.business.enumeration.Role;
import ninco.business.enumeration.State;
import ninco.business.rules.ValidationResult;
import ninco.business.rules.Validator;
import ninco.common.InvalidFieldException;
import ninco.common.UserDisplayableException;
import ninco.gui.AlertFacade;

public class GUIRegisterEmployeeModalController extends Controller {
  @FXML
  private Label labelTagEmail;
  @FXML
  private TextField fieldEmail;
  @FXML
  private TextField fieldName;
  @FXML
  private Label labelTagName;
  @FXML
  private TextField fieldLastName;
  @FXML
  private Label labelTagLastName;
  @FXML
  private ComboBox<Role> fieldRole;
  @FXML
  private ComboBox<State> fieldState;
  @FXML
  private ComboBox<StoreDTO> fieldStore;
  @FXML
  private Label labelTagFieldStore;

  public void initialize() {
    cleanErrorLabels();
    configureFieldRole();
    configureFieldStore();
    configureFieldState();
  }

  private void configureFieldRole() {
    fieldRole.getItems().setAll(Role.values());
    fieldRole.setValue(Role.CASHIER);
  }

  private void configureFieldState() {
    fieldState.getItems().setAll(State.values());
    fieldState.setValue(State.ACTIVE);
  }

  private void configureFieldStore() {
    try {
      fieldStore.getItems().setAll(
        StoreDAO.getInstance().getAll()
      );
    } catch (UserDisplayableException e) {
      AlertFacade.showErrorAndWait(
        "Unable to load stores. It is impossible to register an employee. Please close the modal."
      );
      labelTagFieldStore.setText(
        "Unable to load stores."
      );
    }
  }

  private void cleanErrorLabels() {
    labelTagEmail.setText("");
    labelTagName.setText("");
    labelTagLastName.setText("");
    labelTagFieldStore.setText("");
  }

  private boolean isValidData() {
    boolean isValid = true;

    ValidationResult result = Validator.getIsInvalidEmailResult(fieldEmail.getText());
    if (result.isInvalid()) {
      labelTagEmail.setText(result.getMessage());
      isValid = false;
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

    if (fieldRole.getValue() == null) {
      AlertFacade.showErrorAndWait("Unable to register employee. Please select a Role.");
      isValid = false;
    }

    if (fieldState.getValue() == null) {
      AlertFacade.showErrorAndWait("Unable to register employee. Please select a State.");
      isValid = false;
    }

    if (fieldStore.getValue() == null) {
      labelTagFieldStore.setText("Store must be selected.");
      isValid = false;
    }

    return isValid;
  }

  public AccountDTO getAccountDTOFromInput() throws InvalidFieldException {
    return new AccountDTO(
      fieldEmail.getText(),
      AccountDTO.getGeneratedPassword(),
      fieldRole.getValue(),
      fieldState.getValue()
    );
  }

  public EmployeeDTO getEmployeeDTOFromInput() throws InvalidFieldException {
    return new EmployeeDTO(
      fieldStore.getValue().getID(),
      fieldEmail.getText(),
      fieldRole.getValue(),
      fieldState.getValue(),
      fieldName.getText(),
      fieldLastName.getText()
    );
  }

  public void onClickRegisterEmployee() {
    try {
      cleanErrorLabels();
      if (isValidData()) {
        AccountDTO accountDTO = getAccountDTOFromInput();
        AccountDTO existingAccountDTO = AccountDAO.getInstance().findOne(accountDTO.getEmail());

        if (existingAccountDTO != null) {
          AlertFacade.showErrorAndWait("Unable to register employee. An account with this email already exists.");
          return;
        }

        EmployeeDAO.getInstance().createOne(
          getEmployeeDTOFromInput(),
          accountDTO.getPassword()
        );
        AlertFacade.showSuccessAndWait("Employee registered successfully.");
      }
    } catch (InvalidFieldException | UserDisplayableException e) {
      AlertFacade.showErrorAndWait(e.getMessage());
    }
  }
}
