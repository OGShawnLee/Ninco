package ninco.gui.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import ninco.business.dao.StockDAO;
import ninco.business.dto.StockDTO;
import ninco.common.UserDisplayableException;
import ninco.gui.AlertFacade;

import java.net.URL;
import java.util.ResourceBundle;

public class GUIReviewStockPageController extends Controller implements Initializable {
    @FXML
    private TableView<StockDTO> tableStock;
    @FXML
    private TableColumn<StockDTO, String> columnProductID;
    @FXML
    private TableColumn<StockDTO, String> columnStoreID;
    @FXML
    private TableColumn<StockDTO, String> columnProductName;
    @FXML
    private TableColumn<StockDTO, String> columnStoreName;
    @FXML
    private TableColumn<StockDTO, String> columnQuantity;
    @FXML
    private TableColumn<StockDTO, String> columnFormattedCreatedAt;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureTableColumns();
        setTableItems();
    }

    public void configureTableColumns() {
        columnProductID.setCellValueFactory(new PropertyValueFactory<>("Product ID"));
        columnStoreID.setCellValueFactory(new PropertyValueFactory<>("Store ID"));
        columnProductName.setCellValueFactory(new PropertyValueFactory<>("Product Name"));
        columnStoreName.setCellValueFactory(new PropertyValueFactory<>("Store Name"));
        columnQuantity.setCellValueFactory(new PropertyValueFactory<>("Quantity"));
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
}
