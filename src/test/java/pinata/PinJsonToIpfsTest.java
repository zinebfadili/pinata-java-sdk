package pinata;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import util.RequestSender;

public class PinJsonToIpfsTest {

  @Test
  public void pinJSON() {
    Pinata pinata = new Pinata();
    JSONObject goodJSon = new JSONObject("{ test: 'test'}");
    try (MockedStatic<RequestSender> utilities = Mockito.mockStatic(RequestSender.class)) {
      utilities.when(() -> RequestSender.postOrPutRequest(any(),any(), any(), any(), any()))
          .thenReturn(new JSONObject("{ status: 200 }"));
      JSONObject response = pinata.pinJsonToIpfs("test", "test", goodJSon);
      assertEquals(200, response.getInt("status"));
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }

}
