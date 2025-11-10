package ninco.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import ninco.business.dao.StoreDAO;
import ninco.business.dto.StoreDTO;
import ninco.business.rules.ValidationResult;
import ninco.business.rules.Validator;
import ninco.common.InvalidFieldException;
import ninco.common.UserDisplayableException;
import ninco.gui.AlertFacade;

public class GUIRegisterStoreModalController extends Controller {
  @FXML
  private Label labelTagName;
  @FXML
  private TextField fieldName;
  @FXML
  private Label labelTagAddress;
  @FXML
  private TextField fieldAddress;
  @FXML
  private Label labelTagPhoneNUmber;
  @FXML
  private TextField fieldPhoneNumber;

  public void initialize() {
    cleanErrorLabels();
  }

  public void cleanErrorLabels() {
    labelTagName.setText("");
    labelTagAddress.setText("");
    labelTagPhoneNUmber.setText("");
  }

  public boolean isValidData() {
    boolean isValid = true;

    ValidationResult result = Validator.getIsInvalidNameResult(fieldName.getText(), "Store Name", 3, 128);
    if (result.isInvalid()) {
      labelTagName.setText(result.getMessage());
      isValid = false;
    }

    result = Validator.getIsInvalidAddressResult(fieldAddress.getText());
    if (result.isInvalid()) {
      labelTagAddress.setText(result.getMessage());
      isValid = false;
    }

    result = Validator.getIsInvalidPhoneNumberResult(fieldPhoneNumber.getText());
    if (result.isInvalid()) {
      labelTagPhoneNUmber.setText(result.getMessage());
      isValid = false;
    }

    return isValid;
  }

  public StoreDTO getStoreDTOFromInput() throws InvalidFieldException {
    return new StoreDTO(
      fieldName.getText(),
      fieldAddress.getText(),
      fieldPhoneNumber.getText()
    );
  }

  public void onClickRegisterStore() {
    try {
      cleanErrorLabels();
      if (isValidData()) {
        StoreDTO existingStoreDTO = StoreDAO.getInstance().findOneByPhoneNumber(fieldPhoneNumber.getText());

        if (existingStoreDTO != null) {
          labelTagPhoneNUmber.setText("A store with this phone number already exists.");
          return;
        }

        StoreDAO.getInstance().createOne(getStoreDTOFromInput());
        AlertFacade.showSuccessAndWait("Store registered successfully.");
      }
    } catch (InvalidFieldException | UserDisplayableException e) {
      AlertFacade.showErrorAndWait(e.getMessage());
    }
  }
}
