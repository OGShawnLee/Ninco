package ninco.business.dto;

import ninco.business.enumeration.Role;
import ninco.business.enumeration.State;
import ninco.business.rules.Validator;
import ninco.common.InvalidFieldException;

import java.time.LocalDateTime;

public class EmployeeDTO implements Record, Searchable {
  private int id;
  private int idAccount;
  private int idStore;
  private String nameStore;
  private String email;
  private Role role;
  private State state;
  private String name;
  private String lastName;
  private LocalDateTime createdAt;

  /**
   * Constructor for creating an EmployeeDTO object with all fields from the database.
   *
   * @param id        The unique identifier of the employee
   * @param idAccount The unique identifier of the Account associated with the employee
   * @param idStore   The unique identifier of the store associated with the employee (null if none)
   * @param nameStore The name of the store associated with the employee (null if none)
   * @param email     The email address associated with the employee
   * @param role      The role assigned to the employee (e.g., ADMIN, USER)
   * @param state     The current state of the employee (e.g., ACTIVE, INACTIVE)
   * @param name      The first name of the employee
   * @param lastName  The last name of the employee
   * @param createdAt The timestamp when the employee record was created
   * @throws InvalidFieldException if any of the fields are invalid
   */
  public EmployeeDTO(
    int id,
    int idAccount,
    int idStore,
    String nameStore,
    String email,
    Role role,
    State state,
    String name,
    String lastName,
    LocalDateTime createdAt
  ) throws InvalidFieldException {
    this.idAccount = idAccount;
    this.id = id;
    this.idStore = idStore;

    if (nameStore != null) {
      this.nameStore = Validator.getValidName(nameStore, "store-name", 3, 128);
    }

    this.email = Validator.getValidEmail(email);
    this.role = role;
    this.state = state;
    this.name = Validator.getValidName(name, "name", 3, 64);
    this.lastName = Validator.getValidName(lastName, "last-name", 3, 64);
    this.createdAt = createdAt;
  }

  /**
   * Constructor for creating an EmployeeDTO object without ID and createdAt fields.
   * Typically used when creating a new employee before it is saved to the database.
   *
   * @param idStore  The unique identifier of the store associated with the employee (-1 if none)
   * @param email    The email address associated with the employee
   * @param role     The role assigned to the employee (e.g., ADMIN, USER)
   * @param state    The current state of the employee (e.g., ACTIVE, INACTIVE)
   * @param name     The first name of the employee
   * @param lastName The last name of the employee
   * @throws InvalidFieldException if any of the fields are invalid
   */
  public EmployeeDTO(
    int idStore,
    String email,
    Role role,
    State state,
    String name,
    String lastName
  ) throws InvalidFieldException {
    this.idStore = idStore;
    this.email = Validator.getValidEmail(email);
    this.role = role;
    this.state = state;
    this.name = Validator.getValidName(name, "name", 3, 64);
    this.lastName = Validator.getValidName(lastName, "last-name", 3, 64);
  }

  public EmployeeDTO prepareUpdate(EmployeeDTO employeeDTOFromInput) {
    this.idStore = employeeDTOFromInput.getIDStore();
    this.email = employeeDTOFromInput.getEmail();
    this.role = employeeDTOFromInput.getRole();
    this.state = employeeDTOFromInput.getState();
    this.name = employeeDTOFromInput.getName();
    this.lastName = employeeDTOFromInput.getLastName();

    return this;
  }

  public int getID() {
    return id;
  }

  public int getIDAccount() {
    return idAccount;
  }

  public int getIDStore() {
    return idStore;
  }

  public String getNameStore() {
    return nameStore;
  }

  public String getEmail() {
    return email;
  }

  public Role getRole() {
    return role;
  }

  public State getState() {
    return state;
  }

  public String getName() {
    return name;
  }

  public String getLastName() {
    return lastName;
  }

  @Override
  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  @Override
  public String getSearchableText() {
    return String.format(
      "%s %s %s %s %s %s",
      getName(),
      getLastName(),
      getEmail(),
      getRole(),
      getNameStore(),
      getFormattedCreatedAt()
    ).toLowerCase();
  }
}
