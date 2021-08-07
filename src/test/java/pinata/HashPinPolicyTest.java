package pinata;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import util.RequestSender;

public class HashPinPolicyTest {

  static final String GOOD_HASH = "Qma6e8dovfLyiG2UUfdkSHNPAySzrWLX9qVXb44v1muqcp";
  static final JSONObject newPinPolicy = new JSONObject(
      "{ regions: [ { id: 'FRA1', desiredReplicationCount: 2 }, { id: 'NYC1', desiredReplicationCount: 2 } ] }");

  @Test
  public void noIpfsPinHash() {
    Pinata pinata = new Pinata();
    Exception thrown = assertThrows(Exception.class,
        () -> {
          pinata.hashPinPolicy("test", "test", null, newPinPolicy);
        });
    assertEquals("ipfsPinHash value is required for changing the pin policy of a pin",
        thrown.getMessage());
  }

  @Test
  public void invalidIpfsPinHash() {
    Pinata pinata = new Pinata();
    Exception thrown = assertThrows(Exception.class,
        () -> {
          pinata.hashPinPolicy("test", "test", "test", newPinPolicy);
        });
    assertEquals("hashToPin value is an invalid IPFS CID",
        thrown.getMessage());
  }

  @Test
  public void noNewPinPolicy() {
    Pinata pinata = new Pinata();
    Exception thrown = assertThrows(Exception.class,
        () -> {
          pinata.hashPinPolicy("test", "test", GOOD_HASH, null);
        });
    assertEquals("No pin policy provided",
        thrown.getMessage());
  }

  @Test
  public void validHashPinPolicy() {
    Pinata pinata = new Pinata();
    try (MockedStatic<RequestSender> utilities = Mockito.mockStatic(RequestSender.class)) {
      utilities.when(() -> RequestSender.postOrPutRequest(any(),any(), any(), any(), any()))
          .thenReturn(new JSONObject("{ status: 200 }"));
      JSONObject response = pinata.hashPinPolicy("test", "test", GOOD_HASH, newPinPolicy);
      assertEquals(200, response.getInt("status"));
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
}
