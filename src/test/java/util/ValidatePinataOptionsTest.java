package util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

public class ValidatePinataOptionsTest {

  @Test
  public void cidNotVersionOneOrTwo() {
    JSONObject badVersion = new JSONObject();
    badVersion.put("test", "testing");
    JSONObject options = new JSONObject();
    options.put("cidVersion", badVersion);
    Exception thrown = assertThrows(Exception.class,
        () -> {
          Validator.validatePinataOptions(options);
        });
    assertEquals("unsupported or invalid cidVersion",
        thrown.getMessage());
  }

  @Test
  public void wrapWithDirectoryIsNotBoolean() {
    JSONObject options = new JSONObject();
    options.put("wrapWithDirectory", "test");
    Exception thrown = assertThrows(Exception.class,
        () -> {
          Validator.validatePinataOptions(options);
        });
    assertEquals("wrapWithDirectory must be a boolean value of true or false",
        thrown.getMessage());
  }

}
