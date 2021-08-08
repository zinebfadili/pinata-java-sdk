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

public class UnpinTest {

  @Test
  public void noHashToUnpin() {
    Pinata pinata = new Pinata();
    Exception thrown = assertThrows(Exception.class,
        () -> {
          pinata.unpin("test", "test", null);
        });
    assertEquals("hashToUnpin value is required for removing a pin from Pinata",
        thrown.getMessage());
  }

  @Test
  public void validHashToUnpin() {
    Pinata pinata = new Pinata();
    try (MockedStatic<RequestSender> utilities = Mockito.mockStatic(RequestSender.class)) {
      PinataResponse expectedResponse = new PinataResponse();
      expectedResponse.setStatus(200);
      utilities.when(() -> RequestSender.postOrPutRequest(any(),any(), any(), any(), any()))
          .thenReturn(expectedResponse);
      PinataResponse response = pinata.unpin("test", "test",
          "QmVkauiTpFLVCGXKnZkBB7byohrGwsfFUYBsfsiZb9iBqy");
      assertEquals(200, response.getStatus());
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
}
