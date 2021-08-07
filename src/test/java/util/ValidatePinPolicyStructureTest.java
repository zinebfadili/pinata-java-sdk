package util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

public class ValidatePinPolicyStructureTest {

  @Test
  public void noPolicy() {
    Exception thrown = assertThrows(Exception.class,
        () -> {
          Validator.validatePinPolicyStructure(new JSONObject());
        });
    assertEquals("No pin policy provided",
        thrown.getMessage());
  }

  @Test
  public void noRegions() {
    JSONObject pinPolicy = new JSONObject();
    pinPolicy.put("test", "test");
    Exception thrown = assertThrows(Exception.class,
        () -> {
          Validator.validatePinPolicyStructure(pinPolicy);
        });
    assertEquals("No regions provided in pin policy",
        thrown.getMessage());
  }

  @Test
  public void regionIdNotString() {
    JSONObject pinPolicy = new JSONObject(
        "{ regions: [ { id: 'goodRegionId', desiredReplicationCount: 1 }, { id: 0, desiredReplicationCount: 1 } ] }");
    Exception thrown = assertThrows(Exception.class,
        () -> {
          Validator.validatePinPolicyStructure(pinPolicy);
        });
    assertEquals("region id must be a string",
        thrown.getMessage());
  }

  @Test
  public void replicationCountNotInt() {
    JSONObject pinPolicy = new JSONObject(
        "{ regions: [ { id: 'goodRegionId', desiredReplicationCount: 1 }, { id: 'goodRegionId2', desiredReplicationCount: 'string that should fail' } ] }");
    Exception thrown = assertThrows(Exception.class,
        () -> {
          Validator.validatePinPolicyStructure(pinPolicy);
        });
    assertEquals("desiredReplicationCount must be an integer",
        thrown.getMessage());
  }
}
