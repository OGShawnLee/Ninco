package ninco.business.rules;

public class ValidationResult {
  private boolean isInvalid;
  private String message;

  public ValidationResult(String message) {
    this.isInvalid = true;
    this.message = message;
  }

  public ValidationResult() {
    this.isInvalid = false;
  }

  public boolean isInvalid() {
    return isInvalid;
  }

  public String getMessage() {
    return message;
  }
}