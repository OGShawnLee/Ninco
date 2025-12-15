package ninco.gui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ninco.business.AuthClient;
import ninco.business.dao.InvoiceDAO;
import ninco.business.dao.ProductDAO;
import ninco.business.dao.StockDAO;
import ninco.business.dto.CartItemDTO;
import ninco.business.dto.EmployeeDTO;
import ninco.business.dto.ProductDTO;
import ninco.common.ReceiptGenerator;
import ninco.common.UserDisplayableException;
import ninco.gui.AlertFacade;

import java.util.Optional;

public class GUISalePageController extends Controller implements ContextController<String> {

  @FXML
  private Label labelClientName;
  @FXML
  private Label labelTotal;
  @FXML
  private TextField fieldSearchProduct;
  @FXML
  private TableView<CartItemDTO> tableCart;
  @FXML
  private TableColumn<CartItemDTO, String> colName;
  @FXML
  private TableColumn<CartItemDTO, Float> colPrice;
  @FXML
  private TableColumn<CartItemDTO, Integer> colQuantity;
  @FXML
  private TableColumn<CartItemDTO, Float> colSubtotal;

  private String clientName;
  private ObservableList<CartItemDTO> cartItems;
  private final ProductDAO productDAO = ProductDAO.getInstance();
  private final StockDAO stockDAO = StockDAO.getInstance();

  public void initialize() {
    cartItems = FXCollections.observableArrayList();
    configureTable();
    updateTotal();
  }

  @Override
  public void setContext(String clientName) {
    this.clientName = clientName;
    if (labelClientName != null) {
      labelClientName.setText("Client: " + clientName);
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
      AlertFacade.showWarningAndWait("Please enter a product name or ID.");
      return;
    }

    try {
      ProductDTO product = productDAO.searchByNameOrCode(query);
      if (product == null) {
        AlertFacade.showWarningAndWait("Product not found.");
        return;
      }

      int storeId = AuthClient.getInstance().getCurrentUser().getIDStore();
      int currentStock = stockDAO.getCurrentStockByProduct(storeId, product.getIDProduct());

      if (currentStock <= 0) {
        AlertFacade.showWarningAndWait("Out of stock.");
        return;
      }

      TextInputDialog dialog = new TextInputDialog("1");
      dialog.setTitle("Quantity");
      dialog.setHeaderText("Add: " + product.getName());
      dialog.setContentText("Available: " + currentStock + ". Enter quantity:");

      Optional<String> result = dialog.showAndWait();
      if (result.isPresent()) {
        try {
          int qty = Integer.parseInt(result.get());
          if (qty <= 0) {
            AlertFacade.showWarningAndWait("Quantity must be positive.");
            return;
          }
          if (qty > currentStock) {
            AlertFacade.showWarningAndWait("Not enough stock. Available: " + currentStock);
            return;
          }
          addItemToCart(product, qty);
        } catch (NumberFormatException e) {
          AlertFacade.showWarningAndWait("Invalid number.");
        }
      }

    } catch (Exception e) {
      AlertFacade.showErrorAndWait("Error adding product: " + e.getMessage());
    }
  }

  private void addItemToCart(ProductDTO product, int qty) {
    Optional<CartItemDTO> existing = cartItems.stream()
      .filter(i -> i.getProduct().getIDProduct() == product.getIDProduct())
      .findFirst();

    if (existing.isPresent()) {
      CartItemDTO item = existing.get();
      item.setQuantity(item.getQuantity() + qty);
      tableCart.refresh();
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
    } else {
      AlertFacade.showWarningAndWait("Select an item to remove.");
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
      try {
        navigateFromThisPageTo("Cashier Dashboard", "GUILandingCashierPage");
      } catch (Exception e) {
        AlertFacade.showErrorAndWait("Error navigating back.");
      }
    }
  }

  public void onClickCheckout() {
    if (cartItems.isEmpty()) {
      AlertFacade.showWarningAndWait("Cart is empty.");
      return;
    }

    if (AlertFacade.showConfirmationAndWait("Confirm Sale?")) {
      try {
        EmployeeDTO user = AuthClient.getInstance().getCurrentUser();
        InvoiceDAO.getInstance().createInvoiceTransaction(
          user.getIDStore(),
          user.getID(),
          clientName,
          cartItems
        );

        double total = cartItems.stream().mapToDouble(CartItemDTO::getSubtotal).sum();
        String filePath = ReceiptGenerator.generateReceipt(clientName, cartItems, total);

        AlertFacade.showSuccessAndWait("Sale completed! Receipt saved at:\n" + filePath);

        AlertFacade.showSuccessAndWait("Sale completed!");
        navigateFromThisPageTo("Cashier Dashboard", "GUILandingCashierPage");

      } catch (UserDisplayableException e) {
        AlertFacade.showErrorAndWait(e.getMessage());
      } catch (Exception e) {
        AlertFacade.showErrorAndWait("Unexpected error: " + e.getMessage());
        e.printStackTrace();
      }
    }
  }
}