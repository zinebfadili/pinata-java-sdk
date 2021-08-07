package pinata;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class UserPinPolicyTest {

  @Test
  public void noPinPolicy() {
    Pinata pinata = new Pinata();
    Exception thrown = assertThrows(Exception.class,
        () -> {
          pinata.userPinPolicy("test", "test", null);
        });
    assertEquals("No pin policy provided",
        thrown.getMessage());
  }
}
