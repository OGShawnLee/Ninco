package ninco.common;

public class InvalidFieldException extends Exception {
  private final String field;

  public InvalidFieldException(String message, String field) {
    super(message);
    this.field = field;
  }

  public String getField() {
    return this.field;
  }
}
