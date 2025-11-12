package ninco.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import ninco.business.dao.ProductDAO;
import ninco.business.dao.StoreDAO;
import ninco.business.dto.StoreDTO;
import ninco.business.rules.ValidationResult;
import ninco.business.rules.Validator;
import ninco.common.InvalidFieldException;
import ninco.common.UserDisplayableException;
import ninco.gui.AlertFacade;

public class GUIRegisterStoreModalController extends Controller implements ContextController<StoreDTO> {
  @FXML
  private Label title;
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
  private StoreDTO editStoreDTO;

  @Override
  public void setContext(StoreDTO data) {
    editStoreDTO = data;
    loadEditData();
    configureTitle();
  }

  public void initialize() {
    cleanErrorLabels();
  }

  private void configureTitle() {
    if (editStoreDTO == null) return;

    title.setText("Update Store");
  }

  private void loadEditData() {
    if (editStoreDTO == null) return;

    fieldName.setText(editStoreDTO.getName());
    fieldAddress.setText(editStoreDTO.getAddress());
    fieldPhoneNumber.setText(editStoreDTO.getPhoneNumber());
  }

  private void cleanErrorLabels() {
    labelTagName.setText("");
    labelTagAddress.setText("");
    labelTagPhoneNUmber.setText("");
  }

  private boolean isValidData() {
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

  private StoreDTO getStoreDTOFromInput() throws InvalidFieldException {
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
        if (editStoreDTO == null) {
          StoreDTO existingStoreDTO = StoreDAO.getInstance().findOneByPhoneNumber(fieldPhoneNumber.getText());

          if (existingStoreDTO != null) {
            labelTagPhoneNUmber.setText("A store with this phone number already exists.");
            return;
          }

          StoreDAO.getInstance().createOne(getStoreDTOFromInput());
          AlertFacade.showSuccessAndWait("Store registered successfully.");
        } else {
          if (StoreDAO.getInstance().updateOne(getStoreDTOFromInput(), editStoreDTO)) {
            AlertFacade.showErrorAndWait("Unable to update store, try again later.");
          } else {
            AlertFacade.showSuccessAndWait("Store updated successfully.");
          }
        }
      }
    } catch (InvalidFieldException | UserDisplayableException e) {
      AlertFacade.showErrorAndWait(e.getMessage());
    }
  }
}
