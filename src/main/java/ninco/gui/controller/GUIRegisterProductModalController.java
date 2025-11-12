package ninco.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import ninco.business.dao.ProductDAO;
import ninco.business.dto.ProductDTO;
import ninco.business.rules.ValidationResult;
import ninco.business.rules.Validator;
import ninco.common.InvalidFieldException;
import ninco.common.UserDisplayableException;
import ninco.gui.AlertFacade;

public class GUIRegisterProductModalController extends Controller implements ContextController<ProductDTO> {
  @FXML
  private Label title;
  @FXML
  private Label labelTagName;
  @FXML
  private TextField fieldName;
  @FXML
  private Label labelTagDescription;
  @FXML
  private TextArea fieldDescription;
  @FXML
  private Label labelTagBrand;
  @FXML
  private TextField fieldBrand;
  @FXML
  private Label labelTagPrice;
  @FXML
  private TextField fieldPrice;
  private ProductDTO editProductDTO;

  @Override
  public void setContext(ProductDTO data) {
    editProductDTO = data;
    loadEditData();
    configureTitle();
  }

  public void initialize() {
    cleanErrorLabels();
  }

  private void configureTitle() {
    if (editProductDTO == null) return;

    title.setText("Update Product");
  }

  private void loadEditData() {
    if (editProductDTO == null) return;

    fieldName.setText(editProductDTO.getName());
    fieldDescription.setText(editProductDTO.getDescription());
    fieldBrand.setText(editProductDTO.getBrand());
    fieldPrice.setText(String.valueOf(editProductDTO.getPrice()));
  }

  private void cleanErrorLabels() {
    labelTagName.setText("");
    labelTagDescription.setText("");
    labelTagBrand.setText("");
    labelTagPrice.setText("");
  }

  private boolean isValidData() {
    boolean isValid = true;

    ValidationResult result = Validator.getIsInvalidNameResult(fieldName.getText(), "Name", 3, 128);
    if (result.isInvalid()) {
      labelTagName.setText(result.getMessage());
      isValid = false;
    }

    result = Validator.getIsValidTextResult(fieldDescription.getText(), "Description");
    if (result.isInvalid()) {
      labelTagDescription.setText(result.getMessage());
      isValid = false;
    }

    result = Validator.getIsInvalidNameResult(fieldBrand.getText(), "Brand", 3, 64);
    if (result.isInvalid()) {
      labelTagBrand.setText(result.getMessage());
      isValid = false;
    }

    result = Validator.getIsValidPriceResult(fieldPrice.getText(), "Price");
    if (result.isInvalid()) {
      labelTagPrice.setText(result.getMessage());
      isValid = false;
    }

    return isValid;
  }

  private ProductDTO getProductDTOFromInput() throws InvalidFieldException {
    return new ProductDTO(
      fieldName.getText(),
      fieldDescription.getText(),
      fieldBrand.getText(),
      fieldPrice.getText()
    );
  }

  public void onClickRegisterProduct() {
    try {
      cleanErrorLabels();
      if (isValidData()) {
        if (editProductDTO == null) {
          ProductDAO.getInstance().createOne(getProductDTOFromInput());
          AlertFacade.showSuccessAndWait("Product registered successfully.");
        } else {
          if (ProductDAO.getInstance().updateOne(getProductDTOFromInput(), editProductDTO)) {
            AlertFacade.showErrorAndWait("Unable to update product, try again later.");
          } else {
            AlertFacade.showSuccessAndWait("Product updated successfully.");
          }
        }
      }
    } catch (InvalidFieldException | UserDisplayableException e) {
      AlertFacade.showErrorAndWait(e.getMessage());
    }
  }
}
