package ninco.gui.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
  private TableColumn<EmployeeDTO, String> columnEmployeeID;
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

  public void initialize() {
    configureTableColumns();
    setTableItems();
  }

  public void configureTableColumns() {
    columnEmployeeID.setCellValueFactory(new PropertyValueFactory<>("ID"));
    columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
    columnLastName.setCellValueFactory(new PropertyValueFactory<>("LastName"));
    columnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
    columnRole.setCellValueFactory(new PropertyValueFactory<>("role"));
    columnState.setCellValueFactory(new PropertyValueFactory<>("state"));
    columnStoreName.setCellValueFactory(new PropertyValueFactory<>("NameStore"));
    columnFormattedCreatedAt.setCellValueFactory(new PropertyValueFactory<>("formattedCreatedAt"));
  }

  public void setTableItems() {
    try {
      tableEmployee.setItems(
        FXCollections.observableList(EmployeeDAO.getInstance().getAll())
      );
    } catch (UserDisplayableException e) {
      AlertFacade.showErrorAndWait(
        "No ha sido posible recuperar información debido a un error en la base de datos, intente de nuevo más tarde."
      );
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
    EmployeeDTO selectedEmployee = tableEmployee.getSelectionModel().getSelectedItem();

    if (selectedEmployee == null) {
      AlertFacade.showWarningAndWait(
        "Para realizar esta operación debe seleccionar una fila de la tabla."
      );
    } else {
      ModalFacade.createAndDisplayContextModal(
        new ModalFacadeConfiguration(
          "Update Employee",
          "GUIRegisterEmployeeModal",
          this::setTableItems
        ),
        selectedEmployee
      );
    }
  }

  public static void navigateToEmployeeListPage(Stage currentStage) {
    navigateTo(currentStage, "Employee List", "GUIReviewEmployeeListPage");
  }
}