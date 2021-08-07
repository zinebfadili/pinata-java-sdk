package util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

public class ValidateHostNodesTest {

  @Test
  public void hostNodesNotAnArray() {
    JSONObject nodes = new JSONObject();
    nodes.put("test", "test");
    Exception thrown = assertThrows(Exception.class,
        () -> {
          Validator.validateHostNodes(nodes);
        });
    assertEquals("host_nodes value must be an array",
        thrown.getMessage());
  }

  @Test
  public void invalidHostNode() {
    String validHost = "/ip4/127.0.0.1/tcp/1234/ws/ipfs/QmUjNmr8TgJCn1Ao7DvMy4cjoZU15b9bwSCBLE3vwXiwgj";
    String invalidHost = "invalid host";
    JSONArray nodes = new JSONArray();
    nodes.put(validHost);
    nodes.put(invalidHost);
    Exception thrown = assertThrows(Exception.class,
        () -> {
          Validator.validateHostNodes(nodes);
        });
    assertEquals("host_node array entry: " + invalidHost + " is not a valid peer multiaddr",
        thrown.getMessage());
  }
}
