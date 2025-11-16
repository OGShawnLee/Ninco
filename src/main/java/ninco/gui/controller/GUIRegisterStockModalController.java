package ninco.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import ninco.business.dao.StockDAO;
import ninco.business.dto.ProductDTO;
import ninco.business.dto.StockDTO;
import ninco.business.dao.StoreDAO;
import ninco.business.dto.StoreDTO;
import ninco.business.rules.ValidationResult;
import ninco.business.rules.Validator;
import ninco.common.InvalidFieldException;
import ninco.common.UserDisplayableException;
import ninco.gui.AlertFacade;

public class GUIRegisterStockModalController extends Controller implements ContextController<ProductDTO> {
  @FXML
  private TextField fieldProduct;
  @FXML
  private ComboBox<StoreDTO> fieldStore;
  @FXML
  private Label labelTagStore;
  @FXML
  private TextField fieldQuantity;
  @FXML
  private Label labelTagQuantity;
  private ProductDTO productDTO;

  @Override
  public void setContext(ProductDTO data) {
    productDTO = data;
    configureFieldProduct();
  }

  public void initialize() {
    cleanErrorLabels();
    configureFieldStore();
  }

  private void configureFieldProduct() {
    if (productDTO == null) return;

    fieldProduct.setText(productDTO.getName());
  }

  private void configureFieldStore() {
    try {
      fieldStore.getItems().setAll(
        StoreDAO.getInstance().getAll()
      );
    } catch (UserDisplayableException e) {
      AlertFacade.showErrorAndWait(
        "Unable to load stores. It is impossible to register stock. Please close the modal."
      );
      labelTagStore.setText(
        "Unable to load stores."
      );
    }
  }

  private void cleanErrorLabels() {
    labelTagQuantity.setText("");
    labelTagStore.setText("");
  }

  private boolean isValidData() {
    boolean isValid = true;

    ValidationResult result = Validator.getQuantityValidationResult(fieldQuantity.getText());
    if (result.isInvalid()) {
      labelTagQuantity.setText(result.getMessage());
      isValid = false;
    }

    if (fieldStore.getValue() == null) {
      AlertFacade.showErrorAndWait("Unable to register stock. Please select a Store.");
      isValid = false;
    }

    return isValid;
  }

  private StockDTO getStockDTOFromInput() throws InvalidFieldException {
    return new StockDTO(
      productDTO.getID(),
      fieldStore.getValue().getID(),
      fieldQuantity.getText()
    );
  }

  public void onClickRegisterStock() {
    try {
      cleanErrorLabels();
      if (isValidData()) {
        StockDAO.getInstance().createOne(
          getStockDTOFromInput()
        );
        AlertFacade.showSuccessAndWait("Stock registered successfully.");
      }
    } catch (InvalidFieldException | UserDisplayableException e) {
      AlertFacade.showErrorAndWait(e.getMessage());
    }
  }
}
