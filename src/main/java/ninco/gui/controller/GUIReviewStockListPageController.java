package ninco.gui.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import ninco.business.dao.StockDAO;
import ninco.business.dto.StockDTO;
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
  private TableColumn<StockDTO, String> columnFormattedCreatedAt;

  public void initialize() {
    configureTableColumns();
    setTableItems();
  }

  public void configureTableColumns() {
    columnProductName.setCellValueFactory(new PropertyValueFactory<>("ProductName"));
    columnStoreName.setCellValueFactory(new PropertyValueFactory<>("StoreName"));
    columnQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
    columnFormattedCreatedAt.setCellValueFactory(new PropertyValueFactory<>("formattedCreatedAt"));
  }

  public void setTableItems() {
    try {
      tableStock.setItems(FXCollections.observableList(StockDAO.getInstance().getAll()));
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
