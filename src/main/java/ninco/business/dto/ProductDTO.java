package ninco.business.dto;

import ninco.business.rules.Validator;
import ninco.common.InvalidFieldException;

import java.time.LocalDateTime;

public class ProductDTO implements Record {
  private int id;
  private final String name;
  private final String description;
  private final String brand;
  private final float price;
  private int stock;
  private LocalDateTime createdAt;

  /**
   * Constructor for creating a ProductDTO object with all fields from the database.
   *
   * @param id          The unique identifier of the product
   * @param name        The name of the product
   * @param description The description of the product
   * @param brand       The brand of the product
   * @param price       The price of the product
   * @param stock       The stock quantity of the product across all stores
   * @throws InvalidFieldException if any of the fields are invalid
   */
  public ProductDTO(
    int id,
    String name,
    String description,
    String brand,
    float price,
    int stock,
    LocalDateTime createdAt
  ) throws InvalidFieldException {
    this.id = id;
    this.name = Validator.getValidName(name, "product-name", 3, 128);
    this.description = Validator.getValidText(description, "product-description");
    this.brand = Validator.getValidName(name, "product-brand", 3, 64);
    this.price = Validator.getValidPrice(price, "product-price");
    this.stock = stock;
    this.createdAt = createdAt;
  }

  /**
   * Constructor for creating a ProductDTO object from string inputs.
   *
   * @param name        The name of the product
   * @param description The description of the product
   * @param brand       The brand of the product
   * @param price       The price of the product as a string
   * @throws InvalidFieldException if any of the fields are invalid
   */
  public ProductDTO(String name, String description, String brand, String price) throws InvalidFieldException {
    this.name = Validator.getValidName(name, "product-name", 3, 128);
    this.description = Validator.getValidText(description, "product-description");
    this.brand = Validator.getValidName(name, "product-brand", 3, 64);
    this.price = Validator.getValidPrice(Float.parseFloat(price), "product-price");
  }

  public int getID() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public float getPrice() {
    return price;
  }

  public String getBrand() {
    return brand;
  }

  public int getStock() {
    return stock;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }
}
