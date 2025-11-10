package ninco.gui.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import ninco.business.dao.StoreDAO;
import ninco.business.dto.StoreDTO;
import ninco.common.UserDisplayableException;
import ninco.gui.AlertFacade;
import ninco.gui.modal.ModalFacade;
import ninco.gui.modal.ModalFacadeConfiguration;

public class GUIReviewStoreListPageController extends Controller {
  @FXML
  private TableView<StoreDTO> tableStore;
  @FXML
  private TableColumn<StoreDTO, String> columnStoreID;
  @FXML
  private TableColumn<StoreDTO, String> columnName;
  @FXML
  private TableColumn<StoreDTO, String> columnAddress;
  @FXML
  private TableColumn<StoreDTO, String> columnPhoneNumber;
  @FXML
  private TableColumn<StoreDTO, String> columnEmployeeCount;
  @FXML
  private TableColumn<StoreDTO, String> columnFormattedCreatedAt;

  public void initialize() {
    configureTableColumns();
    setTableItems();
  }

  public void configureTableColumns() {
    columnStoreID.setCellValueFactory(new PropertyValueFactory<>("ID"));
    columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
    columnAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
    columnPhoneNumber.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
    columnEmployeeCount.setCellValueFactory(new PropertyValueFactory<>("employeeCount"));
    columnFormattedCreatedAt.setCellValueFactory(new PropertyValueFactory<>("formattedCreatedAt"));
  }

  public void setTableItems() {
    try {
      tableStore.setItems(
        FXCollections.observableList(StoreDAO.getInstance().getAll())
      );
    } catch (UserDisplayableException e) {
      AlertFacade.showErrorAndWait(
        "No ha sido posible recuperar información debido a un error en la base de datos, intente de nuevo más tarde."
      );
    }
  }

  public void onClickRegisterStore() {
    ModalFacade.createAndDisplay(
      new ModalFacadeConfiguration(
        "Register Staff",
        "GUIRegisterStoreModal",
        this::setTableItems
      )
    );
  }

  public void onClickManageStore() {
    StoreDTO selectedStore = tableStore.getSelectionModel().getSelectedItem();

    if (selectedStore == null) {
      AlertFacade.showWarningAndWait(
        "Para realizar esta operación debe seleccionar una fila de la tabla."
      );
    } else {
      // TODO: Add Manage Store Use Case
    }
  }

  public static void navigateToStoreListPage(Stage currentStage) {
    navigateTo(currentStage, "Store List", "GUIReviewStoreListPage");
  }
}