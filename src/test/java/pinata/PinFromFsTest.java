package pinata;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;

import java.net.URL;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import util.RequestSender;

public class PinFromFsTest {

  @Test
  public void validPath() {
    Pinata pinata = new Pinata();
    String fileName = "hello.txt";
    URL resource = getClass().getClassLoader().getResource(fileName);
    assertNotNull(resource);
    try (MockedStatic<RequestSender> utilities = Mockito.mockStatic(RequestSender.class)) {
      utilities.when(() -> RequestSender.postOrPutRequest(any(),any(), any(), any(), any()))
          .thenReturn(new JSONObject("{ status: 200 }"));
      JSONObject response = pinata
          .pinFromFs("test", "test", resource.getPath(), null);
      assertEquals(200, response.getInt("status"));
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void invalidPath() {
    Pinata pinata = new Pinata();
    Exception thrown = assertThrows(Exception.class,
        () -> {
          pinata.pinFromFs("test", "test", "", null);
        });
    assertEquals("file does not exist",
        thrown.getMessage());
  }
}
