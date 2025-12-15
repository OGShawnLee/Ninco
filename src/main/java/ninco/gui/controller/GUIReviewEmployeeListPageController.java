package ninco.gui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import ninco.business.dao.EmployeeDAO;
import ninco.business.dto.EmployeeDTO;
import ninco.business.enumeration.Role;
import ninco.business.enumeration.State;
import ninco.common.UserDisplayableException;
import ninco.gui.AlertFacade;
import ninco.gui.modal.ModalFacade;
import ninco.gui.modal.ModalFacadeConfiguration;

public class GUIReviewEmployeeListPageController extends Controller {
  @FXML
  private TableView<EmployeeDTO> tableEmployee;
  @FXML
  private TableColumn<EmployeeDTO, String> columnName;
  @FXML
  private TableColumn<EmployeeDTO, String> columnLastName;
  @FXML
  private TableColumn<EmployeeDTO, String> columnEmail;
  @FXML
  private TableColumn<EmployeeDTO, Role> columnRole;
  @FXML
  private TableColumn<EmployeeDTO, State> columnState;
  @FXML
  private TableColumn<EmployeeDTO, State> columnStoreName;
  @FXML
  private TableColumn<EmployeeDTO, String> columnFormattedCreatedAt;
  @FXML
  private TextField fieldSearch;

  public void initialize() {
    configureTableColumns();
    setTableItems();
  }

  public void configureTableColumns() {
    columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
    columnLastName.setCellValueFactory(new PropertyValueFactory<>("LastName"));
    columnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
    columnRole.setCellValueFactory(new PropertyValueFactory<>("role"));
    columnState.setCellValueFactory(new PropertyValueFactory<>("state"));
    columnStoreName.setCellValueFactory(new PropertyValueFactory<>("NameStore"));
    columnFormattedCreatedAt.setCellValueFactory(new PropertyValueFactory<>("formattedCreatedAt"));
  }

  private void configureSearch(ObservableList<EmployeeDTO> employeeDTOObservableList) {
    useConfigureSearch(fieldSearch, employeeDTOObservableList, tableEmployee);
  }

  public void setTableItems() {
    try {
      ObservableList<EmployeeDTO> employeeDTOObservableList = FXCollections.observableList(EmployeeDAO.getInstance().getAll());

      tableEmployee.setItems(employeeDTOObservableList);
      configureSearch(employeeDTOObservableList);
    } catch (UserDisplayableException e) {
      AlertFacade.showErrorAndWait(e);
    }
  }

  public void onClickRegisterEmployee() {
    ModalFacade.createAndDisplay(
      new ModalFacadeConfiguration(
        "Register Employee",
        "GUIRegisterEmployeeModal",
        this::setTableItems
      )
    );
  }

  public void onClickManageEmployee() {
    getSelectedItemFromTable(tableEmployee).ifPresent(it -> {
      ModalFacade.createAndDisplayContextModal(
        new ModalFacadeConfiguration(
          "Update Employee",
          "GUIRegisterEmployeeModal",
          this::setTableItems
        ),
        it
      );
    });
  }

  public static void navigateToEmployeeListPage(Stage currentStage) {
    navigateTo(currentStage, "Employee List", "GUIReviewEmployeeListPage");
  }
}