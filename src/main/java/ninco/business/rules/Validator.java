package ninco.business.rules;

import javafx.util.Pair;
import ninco.common.InvalidFieldException;

public class Validator {
  // SIMPLE EMAIL REGEX
  private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
  // MIN 8 CHARS, MAX 64, AT LEAST ONE UPPERCASE, ONE LOWERCASE, ONE DIGIT, ONE SPECIAL CHAR (!@#$%^&*-+)
  private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*\\-+])[A-Za-z\\d!@#$%^&*\\-+]{8,64}$";
  private static final String PHONE_REGEX = "^(\\+\\d{1,3}[- ]?)?\\d{10,15}$";
  private static final String ADDRESS_REGEX = "^[a-zA-Z0-9\\s,.-]{12,256}$";

  public static boolean isInvalidPhoneNumber(String phoneNumber) {
    return isInvalidString(phoneNumber, 10, 15) || !phoneNumber.trim().matches(PHONE_REGEX);
  }

  public static ValidationResult getIsInvalidPhoneNumberResult(String phoneNumber) {
    if (isInvalidString(phoneNumber)) {
      return new ValidationResult("Phone number cannot be empty.");
    }

    if (phoneNumber.trim().matches(PHONE_REGEX)) {
      return new ValidationResult();
    }

    return new ValidationResult("Phone number format is invalid. Must be 10 to 15 digits, optionally starting with a country code.");
  }

  public static String getValidPhoneNumber(String phoneNumber) throws InvalidFieldException {
    ValidationResult result = getIsInvalidPhoneNumberResult(phoneNumber);

    if (result.isInvalid()) {
      throw new InvalidFieldException(result.getMessage(), "phone-number");
    }

    return phoneNumber.trim();
  }

  public static boolean isInvalidAddress(String address) {
    return isInvalidString(address) || !address.trim().matches(ADDRESS_REGEX);
  }

  public static ValidationResult getIsInvalidAddressResult(String address) {
    if (isInvalidString(address)) {
      return new ValidationResult("Address cannot be empty");
    }

    if (address.trim().matches(ADDRESS_REGEX)) {
      return new ValidationResult();
    }

    return new ValidationResult("Address format is invalid.");
  }

  public static String getValidAddress(String address) throws InvalidFieldException {
    ValidationResult result = getIsInvalidAddressResult(address);

    if (result.isInvalid()) {
      throw new InvalidFieldException(result.getMessage(), "address");
    }

    return address.trim();
  }

  public static boolean isInvalidString(String value) {
    return value == null || value.trim().length() == 0;
  }

  public static boolean isInvalidString(String value, int minLength, int maxLength) {
    if (isInvalidString(value)) {
      return true;
    }

    String trimmedString = value.trim();
    return trimmedString.length() < minLength || trimmedString.length() > maxLength;
  }

  public static boolean isInvalidPassword(String password) {
    return isInvalidString(password) || !password.trim().matches(PASSWORD_REGEX);
  }

  public static ValidationResult getIsInvalidEmailResult(String email) {
    if (isInvalidString(email)) {
      return new ValidationResult("Email cannot be empty");
    }

    if (email.trim().matches(EMAIL_REGEX)) {
      return new ValidationResult();
    }

    return new ValidationResult("Email format is invalid.");
  }

  public static String getValidEmail(String email) throws InvalidFieldException {
    ValidationResult result = getIsInvalidEmailResult(email);

    if (result.isInvalid()) {
      throw new InvalidFieldException(result.getMessage(), "email");
    }

    return email.trim();
  }

  public static ValidationResult getIsInvalidPasswordResult(String password) {
    if (isInvalidPassword(password)) {
      return new ValidationResult(
        "Password must be 8-64 characters long and include at least one uppercase letter, one lowercase letter, one digit, and one special character (!@#$%^&*-+)."
      );
    }

    return new ValidationResult();
  }

  public static String getValidPassword(String password) throws InvalidFieldException {
    ValidationResult result = getIsInvalidPasswordResult(password);

    if (result.isInvalid()) {
      throw new InvalidFieldException(result.getMessage(), "password");
    }

    return password.trim();
  }

  public static ValidationResult getIsInvalidNameResult(String name, String fieldName, int minLength, int maxLength) {
    if (isInvalidString(name, minLength, maxLength)) {
      return new ValidationResult(
        String.format("%s must be between %d and %d characters long.", fieldName, minLength, maxLength)
      );
    }

    return new ValidationResult();
  }

  public static String getValidName(String name, String fieldName, int minLength, int maxLength) throws InvalidFieldException {
    ValidationResult result = getIsInvalidNameResult(name, fieldName, minLength, maxLength);

    if (result.isInvalid()) {
      throw new InvalidFieldException(result.getMessage(), fieldName);
    }

    return name.trim();
  }
}
