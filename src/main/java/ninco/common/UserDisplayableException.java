package ninco.common;

public class UserDisplayableException extends Exception {
  public UserDisplayableException(String message) {
    super(message);
  }

  public UserDisplayableException(String message, Throwable cause) {
    super(message, cause);
  }
}