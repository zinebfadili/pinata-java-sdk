package pinata;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import util.RequestSender;

public class PinFileToIpfsTest {

  @Test
  public void validFile() throws URISyntaxException {
    Pinata pinata = new Pinata();
    String filePath = "hello.txt";
    URL resource = getClass().getClassLoader().getResource(filePath);
    assertNotNull(resource);
    File file = new File(resource.toURI());
    try (MockedStatic<RequestSender> utilities = Mockito.mockStatic(RequestSender.class)) {
      PinataResponse expectedResponse = new PinataResponse();
      expectedResponse.setStatus(200);
      utilities.when(() -> RequestSender.postOrPutRequest(any(),any(), any(), any(), any()))
          .thenReturn(expectedResponse);
      PinataResponse response = pinata.pinFileToIpfs("test", "test", file);
      assertEquals(200, response.getStatus());
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void notAFile() {
    Pinata pinata = new Pinata();
    Exception thrown = assertThrows(Exception.class,
        () -> {
          pinata.pinFileToIpfs("test", "test", new File(""));
        });
    assertEquals("file does not exist",
        thrown.getMessage());
  }
}
