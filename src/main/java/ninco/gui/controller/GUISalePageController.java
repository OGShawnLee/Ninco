package ninco.gui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ninco.business.AuthClient;
import ninco.business.dao.ProductDAO;
import ninco.business.dao.StockDAO;
import ninco.business.dto.CartItemDTO;
import ninco.business.dto.ProductDTO;
import ninco.gui.AlertFacade;
import java.util.Optional;

public class GUISalePageController extends Controller implements ContextController<String> {

    @FXML private Label labelClientName;
    @FXML private Label labelTotal;
    @FXML private TextField fieldSearchProduct;
    @FXML private TableView<CartItemDTO> tableCart;
    @FXML private TableColumn<CartItemDTO, String> colName;
    @FXML private TableColumn<CartItemDTO, Float> colPrice;
    @FXML private TableColumn<CartItemDTO, Integer> colQuantity;
    @FXML private TableColumn<CartItemDTO, Float> colSubtotal;

    private String clientName;
    private ObservableList<CartItemDTO> cartItems;
    private final ProductDAO productDAO = ProductDAO.getInstance();
    private final StockDAO stockDAO = StockDAO.getInstance();

    public void initialize() {
        cartItems = FXCollections.observableArrayList();
        configureTable();
    }

    @Override
    public void setContext(String clientName) {
        this.clientName = clientName;
        if (labelClientName != null) {
            labelClientName.setText("Client: " + clientName);
        } else {
            System.err.println("ADVERTENCIA: labelClientName no se inyectó correctamente.");
        }
    }

    private void configureTable() {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        tableCart.setItems(cartItems);
    }

    public void onClickAddProduct() {
        String query = fieldSearchProduct.getText().trim();
        if (query.isEmpty()) {
            AlertFacade.showWarningAndWait("Please enter a product name.");
            return;
        }

        try {
            ProductDTO product = productDAO.searchByNameOrCode(query);
            if (product == null) {
                AlertFacade.showWarningAndWait("Product not found.");
                return;
            }

            TextInputDialog dialog = new TextInputDialog("1");
            dialog.setTitle("Quantity");
            dialog.setHeaderText("Add " + product.getName());
            dialog.setContentText("Enter quantity:");

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                int qty = Integer.parseInt(result.get());
                addItemToCart(product, qty);
            }

        } catch (Exception e) {
            AlertFacade.showErrorAndWait("Error adding product: " + e.getMessage());
        }
    }

    private void addItemToCart(ProductDTO product, int qty) {
        Optional<CartItemDTO> existing = cartItems.stream()
                .filter(i -> i.getProduct().getID() == product.getID())
                .findFirst();

        if (existing.isPresent()) {
            CartItemDTO item = existing.get();
            item.setQuantity(item.getQuantity() + qty);
        } else {
            cartItems.add(new CartItemDTO(product, qty));
        }
        updateTotal();
        fieldSearchProduct.clear();
    }

    public void onClickRemoveItem() {
        CartItemDTO selected = tableCart.getSelectionModel().getSelectedItem();
        if (selected != null) {
            cartItems.remove(selected);
            updateTotal();
        }
    }

    private void updateTotal() {
        double total = cartItems.stream().mapToDouble(CartItemDTO::getSubtotal).sum();
        if (labelTotal != null) {
            labelTotal.setText(String.format("Total: $%.2f", total));
        }
    }

    public void onClickCancel() {
        if (AlertFacade.showConfirmationAndWait("Cancel sale? Data will be lost.")) {
            navigateToLandingPage();
        }
    }

    public void onClickCheckout() {
        AlertFacade.showInformationAndWait("Checkout logic pending implementation.");
    }
}