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
      utilities.when(() -> RequestSender.getRequest(any(), any(), any()))
          .thenReturn(new JSONObject("{ status: 200 }"));
      JSONObject response = pinata.testAuthentication("test", "test");
      assertEquals(200, response.getInt("status"));
    } catch (Exception e) {
      fail();
    }
  }

}