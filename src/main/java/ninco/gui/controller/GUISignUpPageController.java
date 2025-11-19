package ninco.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import ninco.business.dao.AccountDAO;
import ninco.business.dao.EmployeeDAO;
import ninco.business.dao.PendingRegistrationsDAO;
import ninco.business.dto.AccountDTO;
import ninco.business.dto.EmployeeDTO;
import ninco.business.dto.PendingRegistrationDTO;
import ninco.business.dto.SignUpContext;
import ninco.business.enumeration.Role;
import ninco.business.enumeration.State;
import ninco.business.rules.ValidationResult;
import ninco.business.rules.Validator;
import ninco.common.EmailService;
import ninco.common.InvalidFieldException;
import ninco.common.UserDisplayableException;
import ninco.gui.AlertFacade;
import ninco.gui.modal.ModalFacade;
import ninco.gui.modal.ModalFacadeConfiguration;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;

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

    isValid = isValid(isValid, fieldName, labelTagName, fieldLastName, labelTagLastName);

    return isValid;
  }

  static boolean isValid(boolean isValid, TextField fieldName, Label labelTagName, TextField fieldLastName, Label labelTagLastName) {
    ValidationResult result;
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

        if (AccountDAO.getInstance().findOne(accountDTO.getEmail()) != null) {
          AlertFacade.showErrorAndWait("No es posible registrarse. Ya existe una cuenta con este email.");
          return;
        }

        String pin = String.format("%06d", new Random().nextInt(999999));
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(15);

        String hashedPassword = BCrypt.hashpw(accountDTO.getPassword(), BCrypt.gensalt());

        PendingRegistrationDTO pendingDTO = new PendingRegistrationDTO(
                accountDTO.getEmail(),
                pin,
                expiresAt,
                hashedPassword,
                accountDTO.getRole()
        );

        PendingRegistrationsDAO.getInstance().save(pendingDTO);

        try {
          EmailService.sendVerificationCode(accountDTO.getEmail(), pin);
        } catch (UserDisplayableException e) {
          AlertFacade.showErrorAndWait("Error al enviar correo: " + e.getMessage());
          return;
        }

        SignUpContext context = new SignUpContext(getEmployeeDTOFromInput(), accountDTO.getPassword());

        ModalFacade.createAndDisplayContextModal(
                new ModalFacadeConfiguration("Verificar Cuenta", "GUIAccountVerificationModal", () -> {
                  try {
                    if (AccountDAO.getInstance().findOne(accountDTO.getEmail()) != null) {
                      navigateFromThisPageTo("Log In Page", "GUILogInPage");
                    }
                  } catch (Exception ignored) {
                  }
                }),
                context
        );
      }
    } catch (InvalidFieldException | UserDisplayableException e) {
      AlertFacade.showErrorAndWait(e.getMessage());
    }
  }
}
