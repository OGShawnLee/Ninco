package ninco.business.dto;

import ninco.business.enumeration.Role;
import ninco.business.enumeration.State;
import ninco.business.rules.Validator;
import ninco.common.InvalidFieldException;

import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDateTime;

public class AccountDTO {
  private int id;
  private final String email;
  private final Role role;
  private final State state;
  private LocalDateTime createdAt;
  private final String password;

  /**
   * Constructor for creating an AccountDTO object with all fields from the database.
   *
   * @param id        The unique identifier of the account
   * @param email     The email address associated with the account
   * @param password  The hashed password for the account
   * @param role      The role assigned to the account (e.g., ADMIN, USER)
   * @param state     The current state of the account (e.g., ACTIVE, INACTIVE)
   * @param createdAt The timestamp when the account was created
   * @throws InvalidFieldException if any of the fields are invalid
   */
  public AccountDTO(
    int id,
    String email,
    String password,
    Role role,
    State state,
    LocalDateTime createdAt
  ) throws InvalidFieldException {
    this.id = id;
    this.email = Validator.getValidEmail(email);
    this.password = Validator.getValidPassword(password);
    this.role = role;
    this.state = state;
    this.createdAt = createdAt;
  }

  /**
   * Constructor for creating an AccountDTO object without ID and createdAt fields.
   * Typically used when creating a new account before it is saved to the database.
   *
   * @param email    The email address associated with the account
   * @param password The hashed password for the account
   * @param role     The role assigned to the account (e.g., ADMIN, USER)
   * @param state    The current state of the account (e.g., ACTIVE, INACTIVE)
   * @throws InvalidFieldException if any of the fields are invalid
   */
  public AccountDTO(
    String email,
    String password,
    Role role,
    State state
  ) throws InvalidFieldException {
    this.email = Validator.getValidEmail(email);
    this.password = Validator.getValidPassword(password);
    this.role = role;
    this.state = state;
  }

  public int getID() {
    return id;
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

  public String getPassword() {
    return password;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  /** Checks if the provided candidate password matches the stored hashed password.
   *
   * @param candidate The plaintext password to verify
   * @return true if the candidate password matches the stored password, false otherwise
   */
  public boolean hasPasswordMatch(String candidate) {
    return BCrypt.checkpw(candidate, this.password);
  }
}
