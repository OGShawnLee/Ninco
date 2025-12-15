package ninco.gui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import ninco.business.AuthClient;
import ninco.business.dao.StockDAO;
import ninco.business.dao.StoreDAO;
import ninco.business.dto.EmployeeDTO;
import ninco.business.dto.StockDTO;
import ninco.business.dto.StoreDTO;
import ninco.business.enumeration.Role;
import ninco.common.UserDisplayableException;
import ninco.gui.AlertFacade;

public class GUIReviewStockListPageController extends Controller {
  @FXML
  private TableView<StockDTO> tableStock;
  @FXML
  private TableColumn<StockDTO, String> columnProductName;
  @FXML
  private TableColumn<StockDTO, String> columnStoreName;
  @FXML
  private TableColumn<StockDTO, String> columnQuantity;
  @FXML
  private TableColumn<StockDTO, String> columnPrice;
  @FXML
  private TableColumn<StockDTO, String> columnFormattedCreatedAt;
  @FXML
  private TextField fieldSearch;
  @FXML
  private Button buttonStartNewInvoice;

  public void initialize() {
    configureTableColumns();
    configureButtonList();
    setTableItems();
  }

  private void configureButtonList() {
    EmployeeDTO currentUserDTO = AuthClient.getInstance().getCurrentUser();
    buttonStartNewInvoice.setVisible(currentUserDTO.getRole() == Role.CASHIER);
  }

  private void configureTableColumns() {
    columnProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
    columnStoreName.setCellValueFactory(new PropertyValueFactory<>("storeName"));
    columnQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
    columnPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
    columnFormattedCreatedAt.setCellValueFactory(new PropertyValueFactory<>("formattedCreatedAt"));
  }

  private void configureSearch(ObservableList<StockDTO> stockDTOObservableList) {
    useConfigureSearch(fieldSearch, stockDTOObservableList, tableStock);
  }

  private void setTableItems() {
    EmployeeDTO currentUserDTO = AuthClient.getInstance().getCurrentUser();
    try {
      ObservableList<StockDTO> stockDTOObservableList;

      if (currentUserDTO.getRole() == Role.CASHIER) {
        StoreDTO storeDTO = StoreDAO.getInstance().getOne(currentUserDTO.getIDStore());
        stockDTOObservableList = FXCollections.observableList(StockDAO.getInstance().getAllByStore(storeDTO));
      } else {
        stockDTOObservableList = FXCollections.observableList(StockDAO.getInstance().getAll());
      }

      tableStock.setItems(stockDTOObservableList);

      configureSearch(stockDTOObservableList);
    } catch (UserDisplayableException e) {
      AlertFacade.showErrorAndWait(
        "No ha sido posible recuperar información debido a un error en la base de datos, intente de nuevo más tarde."
      );
    }
  }

  public static void navigateToStockListPage(Stage currentStage) {
    navigateTo(currentStage, "Stock List", "GUIReviewStockListPage");
  }
}
