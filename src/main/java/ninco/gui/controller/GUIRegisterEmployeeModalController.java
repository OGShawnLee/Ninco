package ninco.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import ninco.business.AuthClient;
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

import java.util.Optional;

public class GUIRegisterEmployeeModalController extends Controller implements ContextController<EmployeeDTO> {
  @FXML
  private Label title;
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
  private EmployeeDTO editEmployeeDTO;

  @Override
  public void setContext(EmployeeDTO data) {
    editEmployeeDTO = data;
    loadEditData();
    configureTitle();
  }

  public void initialize() {
    cleanErrorLabels();
    configureFieldRole();
    configureFieldStore();
    configureFieldState();
  }

  private void configureTitle() {
    if (editEmployeeDTO == null) return;

    title.setText("Update Employee");
  }

  private void loadEditData() {
    if (editEmployeeDTO == null) return;

    fieldName.setText(editEmployeeDTO.getName());
    fieldLastName.setText(editEmployeeDTO.getLastName());
    fieldEmail.setText(editEmployeeDTO.getEmail());
    fieldRole.setValue(editEmployeeDTO.getRole());
    fieldState.setValue(editEmployeeDTO.getState());
    getStoreFromStoreID(editEmployeeDTO.getIDStore()).ifPresent(
      storeDTO -> fieldStore.setValue(storeDTO)
    );
  }

  private Optional<StoreDTO> getStoreFromStoreID(int storeID) {
    return fieldStore.getItems().stream().filter(it -> it.getID() == storeID).findFirst();
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

      isValid = GUISignUpPageController.isValid(isValid, fieldName, labelTagName, fieldLastName, labelTagLastName);

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

  private AccountDTO getAccountDTOFromInput() throws InvalidFieldException {
    return new AccountDTO(
      fieldEmail.getText(),
      AccountDTO.getGeneratedPassword(),
      fieldRole.getValue(),
      fieldState.getValue()
    );
  }

  private EmployeeDTO getEmployeeDTOFromInput() throws InvalidFieldException {
    return new EmployeeDTO(
      fieldStore.getValue().getID(),
      fieldEmail.getText(),
      fieldRole.getValue(),
      fieldState.getValue(),
      fieldName.getText(),
      fieldLastName.getText()
    );
  }

  private void registerEmployee(AccountDTO accountDTOFromInput) throws InvalidFieldException, UserDisplayableException {
    AccountDTO existingAccountDTO = AccountDAO.getInstance().findOne(accountDTOFromInput.getEmail());

    if (existingAccountDTO != null) {
      AlertFacade.showErrorAndWait("Unable to register employee. An account with this email already exists.");
      return;
    }

    EmployeeDAO.getInstance().createOne(
      getEmployeeDTOFromInput(),
      accountDTOFromInput.getPassword()
    );
    AlertFacade.showSuccessAndWait("Employee registered successfully.");
  }

  private void updateEmployee() throws InvalidFieldException, UserDisplayableException {
    EmployeeDTO employeeDTOFromInput = getEmployeeDTOFromInput();
    Role originalRole = editEmployeeDTO.getRole();
    boolean shallUpdate = true;

    if (originalRole != employeeDTOFromInput.getRole()) {
      if (originalRole == Role.CASHIER) {
        shallUpdate = AlertFacade.showConfirmationAndWait("Are you sure you want to promote this employee from Cashier to Admin?");
      } else {
        shallUpdate = AlertFacade.showConfirmationAndWait("Are you sure you want to demote this employee from Admin to Cashier?");
      }
    }

    State originalState = editEmployeeDTO.getState();
    if (shallUpdate && originalState != employeeDTOFromInput.getState()) {
      if (originalState == State.ACTIVE) {
        shallUpdate = AlertFacade.showConfirmationAndWait("Are you sure you want to remove this employee's access?");
      } else {
        shallUpdate = AlertFacade.showConfirmationAndWait("Are you sure you want to restore this employee's access?");
      }
    }

    int originalStore = editEmployeeDTO.getIDStore();
    if (shallUpdate && originalStore != employeeDTOFromInput.getIDStore()) {
      shallUpdate = AlertFacade.showConfirmationAndWait("Are you sure you want to update this employee's store?");
    }

    if (shallUpdate) {
      EmployeeDAO.getInstance().updateOne(
        editEmployeeDTO.prepareUpdate(getEmployeeDTOFromInput())
      );
      AlertFacade.showSuccessAndWait("Employee updated successfully.");
    }
  }

  public void onClickRegisterEmployee() {
    try {
      cleanErrorLabels();
      if (isValidData()) {
        AccountDTO accountDTO = getAccountDTOFromInput();
        if (editEmployeeDTO == null) {
          registerEmployee(accountDTO);
        } else {
          updateEmployee();
        }
      }
    } catch (InvalidFieldException | UserDisplayableException e) {
      AlertFacade.showErrorAndWait(e.getMessage());
    }
  }
}
