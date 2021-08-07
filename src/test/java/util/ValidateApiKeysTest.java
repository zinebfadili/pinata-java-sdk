package util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class ValidateApiKeysTest {

  @Test
  public void emptyApiKey() {
    Exception thrown = assertThrows(Exception.class,
        () -> {
          Validator.validateApiKeys("", "test");
        });
    assertEquals(
        "No pinataApiKey provided! Please provide your pinata api key as an argument when you start this script",
        thrown.getMessage());
  }

  @Test
  public void emptySecretApiKey() {
    Exception thrown = assertThrows(Exception.class,
        () -> {
          Validator.validateApiKeys("test", "");
        });
    assertEquals(
        "No pinataSecretApiKey provided! Please provide your pinata secret api key as an argument when you start this script",
        thrown.getMessage());
  }
}
