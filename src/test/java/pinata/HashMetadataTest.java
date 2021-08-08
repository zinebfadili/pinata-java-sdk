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

public class HashMetadataTest {

  static final String GOOD_HASH = "Qmc5gCcjYypU7y28oCALwfSvxCBskLuPKWpK4qpterKC7z";
  final JSONObject metadata = new JSONObject(
      "{ name: 'testname', keyvalues: { newKey: 'newValue' } }");


  @Test
  public void missingIpfsPinHash() {
    Pinata pinata = new Pinata();
    Exception thrown = assertThrows(Exception.class,
        () -> {
          pinata.hashMetaData("test", "test", null, metadata);
        });
    assertEquals("ipfsPinHash value is required for changing the pin policy of a pin",
        thrown.getMessage());
  }

  @Test
  public void invalidIpfsPinHash() {
    Pinata pinata = new Pinata();
    Exception thrown = assertThrows(Exception.class,
        () -> {
          pinata.hashMetaData("test", "test", "test", metadata);
        });
    assertEquals("hashToPin value is an invalid IPFS CID",
        thrown.getMessage());
  }

  @Test
  public void noMetadata() {
    Pinata pinata = new Pinata();
    Exception thrown = assertThrows(Exception.class,
        () -> {
          pinata.hashMetaData("test", "test", GOOD_HASH, null);
        });
    assertEquals("no metadata object provided",
        thrown.getMessage());
  }


  @Test
  public void oneKeyValue() {
    Pinata pinata = new Pinata();
    try (MockedStatic<RequestSender> utilities = Mockito.mockStatic(RequestSender.class)) {
      PinataResponse expectedResponse = new PinataResponse();
      expectedResponse.setStatus(200);
      utilities.when(() -> RequestSender.postOrPutRequest(any(),any(), any(), any(), any()))
          .thenReturn(expectedResponse);
      PinataResponse response = pinata.hashMetaData("test", "test", GOOD_HASH, metadata);
      assertEquals(200, response.getStatus());
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void twoKeyValue() {
    Pinata pinata = new Pinata();
    JSONObject metadataTwo = new JSONObject(
        "{ name: 'testname', keyvalues: { newKey: 'newValue', secondKey: 'secondValue' } }");
    try (MockedStatic<RequestSender> utilities = Mockito.mockStatic(RequestSender.class)) {
      PinataResponse expectedResponse = new PinataResponse();
      expectedResponse.setStatus(200);
      utilities.when(() -> RequestSender.postOrPutRequest(any(),any(), any(), any(), any()))
          .thenReturn(expectedResponse);
      PinataResponse response = pinata.hashMetaData("test", "test", GOOD_HASH, metadataTwo);
      assertEquals(200, response.getStatus());
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }


}
