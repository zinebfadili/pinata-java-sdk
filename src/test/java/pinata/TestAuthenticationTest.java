package pinata;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import util.RequestSender;

public class TestAuthenticationTest {

  @Test
  public void testKeys() {
    Pinata pinata = new Pinata();
    try (MockedStatic<RequestSender> utilities = Mockito.mockStatic(RequestSender.class)) {
      PinataResponse expectedResponse = new PinataResponse();
      expectedResponse.setStatus(200);
      utilities.when(() -> RequestSender.getRequest(any(), any(), any()))
          .thenReturn(expectedResponse);
      PinataResponse response = pinata.testAuthentication("test", "test");
      assertEquals(200, response.getStatus());
    } catch (Exception e) {
      fail();
    }
  }

}
