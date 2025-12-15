package ninco.business.dto;

import ninco.business.rules.Validator;
import ninco.common.InvalidFieldException;

import java.time.LocalDateTime;

public class StoreDTO implements Record, Searchable {
  private int id;
  private final String name;
  private final String address;
  private final String phoneNumber;
  private LocalDateTime createdAt;
  private int employeeCount;

  /**
   * Constructor for creating a StoreDTO object with all fields from the database.
   *
   * @param id            The unique identifier of the store
   * @param name          The name of the store
   * @param address       The address of the store
   * @param phoneNumber   The phone number of the store
   * @param createdAt     The creation timestamp of the store
   * @param employeeCount The number of employees in the store
   * @throws InvalidFieldException if any of the fields are invalid
   */
  public StoreDTO(
    int id,
    String name,
    String address,
    String phoneNumber,
    LocalDateTime createdAt,
    int employeeCount
  ) throws InvalidFieldException {
    this.id = id;
    this.name = Validator.getValidName(name, "store-name", 3, 128);
    this.address = Validator.getValidAddress(address);
    this.phoneNumber = Validator.getValidPhoneNumber(phoneNumber);
    this.createdAt = createdAt;
    this.employeeCount = employeeCount;
  }

  /**
   * Constructor for creating a StoreDTO object without an ID (e.g., for new stores).
   *
   * @param name        The name of the store
   * @param address     The address of the store
   * @param phoneNumber The phone number of the store
   * @throws InvalidFieldException if any of the fields are invalid
   */
  public StoreDTO(
    String name,
    String address,
    String phoneNumber
  ) throws InvalidFieldException {
    this.name = Validator.getValidName(name, "store-name", 3, 128);
    this.address = Validator.getValidAddress(address);
    this.phoneNumber = Validator.getValidPhoneNumber(phoneNumber);
  }

  public int getID() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getAddress() {
    return address;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  @Override
  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public int getEmployeeCount() {
    return employeeCount;
  }

  @Override
  public String toString() {
    return String.format("%s (%s)", name, address);
  }

  @Override
  public String getSearchableText() {
    return String.format(
      "%s %s %s %d %s",
      getName(),
      getAddress(),
      getPhoneNumber(),
      getEmployeeCount(),
      getFormattedCreatedAt()
    ).toLowerCase();
  }
}
