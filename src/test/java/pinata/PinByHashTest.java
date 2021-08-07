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

public class PinByHashTest {

  @Test
  public void invalidHash() {
    Pinata pinata = new Pinata();
    Exception thrown = assertThrows(Exception.class,
        () -> {
          pinata.pinByHash("test", "test", "test");
        });
    assertEquals("hashToPin value is an invalid IPFS CID",
        thrown.getMessage());
  }

  @Test
  public void validHash() {
    Pinata pinata = new Pinata();
    try (MockedStatic<RequestSender> utilities = Mockito.mockStatic(RequestSender.class)) {
      utilities.when(() -> RequestSender.postOrPutRequest(any(),any(), any(), any(), any()))
          .thenReturn(new JSONObject("{ status: 200 }"));
      JSONObject response = pinata.pinByHash("test", "test",
          "Qma6e8dovfLyiG2UUfdkSHNPAySzrWLX9qVXb44v1muqcp");
      assertEquals(200, response.getInt("status"));
    } catch (Exception e) {
      fail();
    }
  }

}
