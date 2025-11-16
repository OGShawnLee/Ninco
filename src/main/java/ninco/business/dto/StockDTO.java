package ninco.business.dto;

import ninco.business.rules.Validator;
import ninco.common.InvalidFieldException;

import java.time.LocalDateTime;

public class StockDTO implements Record {
  private final int idProduct;
  private final int idStore;
  private String productName;
  private String storeName;
  private final int quantity;
  private LocalDateTime createdAt;

  /**
   * Constructor for creating a StockDTO object with all fields from the database.
   *
   * @param idProduct   The unique identifier of the product
   * @param idStore     The unique identifier of the store
   * @param productName The name of the product
   * @param storeName   The name of the store
   * @param quantity    The quantity of every product
   * @throws InvalidFieldException if any of the fields are invalid
   */
  public StockDTO(
    int idProduct,
    int idStore,
    String productName,
    String storeName,
    int quantity,
    LocalDateTime createdAt
  ) throws InvalidFieldException {
    this.idProduct = idProduct;
    this.idStore = idStore;
    this.productName = Validator.getValidName(productName, "product-name", 3, 128);
    this.storeName = Validator.getValidName(storeName, "store-name", 3, 128);
    this.quantity = quantity;
    this.createdAt = createdAt;
  }

  /**
   * Constructor for creating a StockDTO object without productName, storeName and createdAt fields.
   * Typically used when creating a new employee before it is saved to the database.
   *
   * @param idProduct The unique identifier of the product
   * @param idStore   The unique identifier of the store
   * @param quantity  The quantity of every product
   * @throws InvalidFieldException if any of the fields are invalid
   */
  public StockDTO(int idProduct, int idStore, String quantity) throws InvalidFieldException {
    this.idProduct = idProduct;
    this.idStore = idStore;
    this.quantity = Validator.getValidQuantity(quantity);
  }

  public int getIDProduct() {
    return idProduct;
  }

  public int getIDStore() {
    return idStore;
  }

  public String getProductName() {
    return productName;
  }

  public String getStoreName() {
    return storeName;
  }

  public int getQuantity() {
    return quantity;
  }

  @Override
  public LocalDateTime getCreatedAt() {
    return createdAt;
  }
}
