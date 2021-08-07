package util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class ValidateMetadataTest {

  @ParameterizedTest
  @CsvSource( value ={
    "{ name: { test: 'testing' }}|metadata name must be of type string",
      "{ keyvalues: 'testing' }|metatadata keyvalues must be an object",
      "{ keyvalues: { one: '1', two: '2', three: '3', four: '4', five: '5', six: 6, seven: 7,"
          + " eight: 8, nine: 9, ten: 10, eleven: 11 }}|"
          + "No more than 10 keyvalues can be provided for metadata entries"
  }, delimiter = '|')
  public void invalidMetadata(JSONObject metadata, String errorMessage) {
    Exception thrown = assertThrows(Exception.class,
        () -> {
          Validator.validateMetadata(metadata);
        });
    assertEquals(errorMessage, thrown.getMessage());
  }

  @Test
  public void objectAsKeyValue() {
    JSONObject invalidValues = new JSONObject(
        "{ one: 1, two: { test: 'test' }, three: 3, four: 4, five: 5, six: 6, seven: 7,"
            + " eight: 8, nine: 9, ten: 10, eleven: 11 }");
    JSONObject metadata = new JSONObject();
    metadata.put("keyvalues", invalidValues);
    Exception thrown = assertThrows(Exception.class,
        () -> {
          Validator.validateMetadata(metadata);
        });
    assertEquals("Metadata keyvalue values must be strings, booleans, or numbers",
        thrown.getMessage());
  }

  @Test
  public void validMetadata() {
    JSONObject validValues = new JSONObject(
        "{ one: 1, two: 2, three: 3, four: 4, five: 5, six: 6, seven: 7, eight: 8, nine: 9, ten: 10 }");
    JSONObject metadata = new JSONObject();
    metadata.put("keyvalues", validValues);
    try {
      Validator.validateMetadata(metadata);
    } catch (Exception e) {
      fail();
    }
  }
}
