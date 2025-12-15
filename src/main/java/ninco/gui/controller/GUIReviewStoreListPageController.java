package ninco.gui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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
  private TableColumn<StoreDTO, String> columnName;
  @FXML
  private TableColumn<StoreDTO, String> columnAddress;
  @FXML
  private TableColumn<StoreDTO, String> columnPhoneNumber;
  @FXML
  private TableColumn<StoreDTO, String> columnEmployeeCount;
  @FXML
  private TableColumn<StoreDTO, String> columnFormattedCreatedAt;
  @FXML
  private TextField fieldSearch;

  public void initialize() {
    configureTableColumns();
    setTableItems();
  }

  public void configureTableColumns() {
    columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
    columnAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
    columnPhoneNumber.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
    columnEmployeeCount.setCellValueFactory(new PropertyValueFactory<>("employeeCount"));
    columnFormattedCreatedAt.setCellValueFactory(new PropertyValueFactory<>("formattedCreatedAt"));
  }

  private void configureSearch(ObservableList<StoreDTO> storeDTOObservableList) {
    useConfigureSearch(fieldSearch, storeDTOObservableList, tableStore);
  }

  public void setTableItems() {
    try {
      ObservableList<StoreDTO> storeDTOObservableList = FXCollections.observableList(StoreDAO.getInstance().getAll());

      tableStore.setItems(storeDTOObservableList);
      configureSearch(storeDTOObservableList);
    } catch (UserDisplayableException e) {
      AlertFacade.showErrorAndWait(e);
    }
  }


  public void onClickRegisterStore() {
    ModalFacade.createAndDisplay(
      new ModalFacadeConfiguration(
        "Register Store",
        "GUIRegisterStoreModal",
        this::setTableItems
      )
    );
  }

  public void onClickManageStore() {
    getSelectedItemFromTable(tableStore).ifPresent(it -> {
      ModalFacade.createAndDisplayContextModal(
        new ModalFacadeConfiguration(
          "Update Store",
          "GUIRegisterStoreModal",
          this::setTableItems
        ),
        it
      );
    });
  }

  public static void navigateToStoreListPage(Stage currentStage) {
    navigateTo(currentStage, "Store List", "GUIReviewStoreListPage");
  }
}