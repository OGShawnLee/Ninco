package ninco.gui.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import ninco.business.dao.ProductDAO;
import ninco.business.dto.ProductDTO;
import ninco.common.UserDisplayableException;
import ninco.gui.AlertFacade;
import ninco.gui.modal.ModalFacade;
import ninco.gui.modal.ModalFacadeConfiguration;

public class GUIReviewProductListPageController extends Controller {
  @FXML
  private TableView<ProductDTO> tableProduct;
  @FXML
  private TableColumn<ProductDTO, String> columnProductID;
  @FXML
  private TableColumn<ProductDTO, String> columnName;
  @FXML
  private TableColumn<ProductDTO, String> columnDescription;
  @FXML
  private TableColumn<ProductDTO, String> columnBrand;
  @FXML
  private TableColumn<ProductDTO, String> columnPrice;
  @FXML
  private TableColumn<ProductDTO, String> columnStock;
  @FXML
  private TableColumn<ProductDTO, String> columnFormattedCreatedAt;

  public void initialize() {
    configureTableColumns();
    setTableItems();
  }

  public void configureTableColumns() {
    columnProductID.setCellValueFactory(new PropertyValueFactory<>("ID"));
    columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
    columnDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
    columnBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
    columnPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
    columnStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
    columnFormattedCreatedAt.setCellValueFactory(new PropertyValueFactory<>("formattedCreatedAt"));
  }

  public void setTableItems() {
    try {
      tableProduct.setItems(
        FXCollections.observableList(ProductDAO.getInstance().getAll())
      );
    } catch (UserDisplayableException e) {
      AlertFacade.showErrorAndWait(
        "No ha sido posible recuperar información debido a un error en la base de datos, intente de nuevo más tarde."
      );
    }
  }

  public void onClickRegisterProduct() {
    ModalFacade.createAndDisplay(
      new ModalFacadeConfiguration(
        "Register Product",
        "GUIRegisterProductModal",
        this::setTableItems
      )
    );
  }

  public void onClickManageProduct() {
    ProductDTO selectedProduct = tableProduct.getSelectionModel().getSelectedItem();

    if (selectedProduct == null) {
      AlertFacade.showWarningAndWait(
        "Para realizar esta operación debe seleccionar una fila de la tabla."
      );
    } else {
      ModalFacade.createAndDisplayContextModal(
        new ModalFacadeConfiguration(
          "Update Product",
          "GUIRegisterProductModal",
          this::setTableItems
        ),
        selectedProduct
      );
    }
  }

  public static void navigateToProductListPage(Stage currentStage) {
    navigateTo(currentStage, "Product List", "GUIReviewProductListPage");
  }
}