package business.rules;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ninco.business.rules.Validator;

public class ValidatorTest {
  @Test
  public void isInvalidStringEmpty() {
    Assertions.assertTrue(Validator.isInvalidString(""));
  }

  @Test
  public void isInvalidStringWhitespace() {
    Assertions.assertTrue(Validator.isInvalidString("   "));
  }

  @Test
  public void isInvalidStringValid() {
    Assertions.assertFalse(Validator.isInvalidString("Valid String"));
  }

  @Test
  public void isInvalidStringWithLengthTooShort() {
    Assertions.assertTrue(Validator.isInvalidString("abc", 5, 10));
  }

  @Test
  public void isInvalidStringWithLengthTooLong() {
    Assertions.assertTrue(Validator.isInvalidString("abcdefghijklmno", 5, 10));
  }

  @Test
  public void isInvalidEmailEmpty() {
    Assertions.assertTrue(Validator.isInvalidEmail(""));
  }

  @Test
  public void isInvalidEmailWhitespace() {
    Assertions.assertTrue(Validator.isInvalidEmail("   "));
  }

  @Test
  public void isInvalidEmailInvalidFormat() {
    Assertions.assertTrue(Validator.isInvalidEmail("invalid-email"));
  }

  @Test
  public void isInvalidEmailValid() {
    Assertions.assertFalse(Validator.isInvalidEmail("daniel@g.com"));
  }

  @Test
  public void isInvalidPasswordEmpty() {
    Assertions.assertTrue(Validator.isInvalidPassword(""));
  }

  @Test
  public void isInvalidPasswordWhitespace() {
    Assertions.assertTrue(Validator.isInvalidPassword("   "));
  }

  @Test
  public void isInvalidPasswordInvalidFormat() {
    Assertions.assertTrue(Validator.isInvalidPassword("password"));
  }

  @Test
  public void isInvalidPasswordInvalidLength() {
    Assertions.assertTrue(Validator.isInvalidPassword("Valid1!"));
  }

  @Test
  public void isInvalidPasswordValid() {
    Assertions.assertFalse(Validator.isInvalidPassword("ValidPass1!"));
  }
}
