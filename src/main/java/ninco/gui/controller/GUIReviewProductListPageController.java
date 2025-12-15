package ninco.gui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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
  @FXML
  private TextField fieldSearch;

  public void initialize() {
    configureTableColumns();
    setTableItems();
  }

  public void configureTableColumns() {
    columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
    columnDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
    columnBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
    columnPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
    columnStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
    columnFormattedCreatedAt.setCellValueFactory(new PropertyValueFactory<>("formattedCreatedAt"));
  }

  private void configureSearch(ObservableList<ProductDTO> productDTOObservableList) {
    useConfigureSearch(fieldSearch, productDTOObservableList, tableProduct);
  }

  public void setTableItems() {
    try {
      ObservableList<ProductDTO> productDTOObservableList = FXCollections.observableList(ProductDAO.getInstance().getAll());

      tableProduct.setItems(productDTOObservableList);
      configureSearch(productDTOObservableList);
    } catch (UserDisplayableException e) {
      AlertFacade.showErrorAndWait(e);
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
    getSelectedItemFromTable(tableProduct).ifPresent(it -> {
      ModalFacade.createAndDisplayContextModal(
        new ModalFacadeConfiguration(
          "Update Product",
          "GUIRegisterProductModal",
          this::setTableItems
        ),
        it
      );
    });
  }

  public void onClickManageStock() {
    getSelectedItemFromTable(tableProduct).ifPresent(it -> {
      ModalFacade.createAndDisplayContextModal(
        new ModalFacadeConfiguration(
          "Register Stock",
          "GUIRegisterStockModal",
          this::setTableItems
        ),
        it
      );
    });
  }

  public static void navigateToProductListPage(Stage currentStage) {
    navigateTo(currentStage, "Product List", "GUIReviewProductListPage");
  }
}