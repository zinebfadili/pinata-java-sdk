package util;

import java.util.Iterator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import pinata.PinataException;

public class Validator {

  private Validator() throws IllegalAccessException {
    throw new IllegalAccessException("Validator class");
  }

  public static void validateApiKeys(String pinataApiKey, String pinataSecretApiKey)
      throws PinataException {
    if (StringUtils.isBlank(pinataApiKey)) {
      throw new PinataException(
          "No pinataApiKey provided! Please provide your pinata api key as an argument "
              + "when you start this script");
    }

    if (StringUtils.isBlank(pinataSecretApiKey)) {
      throw new PinataException(
          "No pinataSecretApiKey provided! Please provide your pinata secret api key as an "
              + "argument when you start this script");
    }
  }

  public static void validateMetadata(JSONObject metadata) throws PinataException {
    if (metadata.has("name")) {
      try {
        metadata.getString("name");
      } catch (Exception e) {
        throw new PinataException("metadata name must be of type string");
      }
    }

    final String keyValues = "keyvalues";
    if (metadata.has(keyValues)) {
      try {
        metadata.getJSONObject(keyValues);
      } catch (Exception e) {
        throw new PinataException("metatadata keyvalues must be an object");
      }

      int i = 0;

      JSONObject values = metadata.getJSONObject(keyValues);
      Iterator<String> keys = values.keys();
      while (keys.hasNext()) {
        if (i > 9) {
          throw new
              PinataException("No more than 10 keyvalues can be provided for metadata entries");
        }
        String key = keys.next();
        Object value = values.get(key);
        if (!(value instanceof String || value instanceof Boolean || NumberUtils
            .isCreatable(value.toString()))) {
          throw new
              PinataException("Metadata keyvalue values must be strings, booleans, or numbers");
        }
        i++;
      }

    }
  }

  public static void validatePinPolicyStructure(JSONObject pinPolicy) throws PinataException {
    if (pinPolicy == null || pinPolicy.length() == 0) {
      throw new PinataException("No pin policy provided");
    }

    final String regions = "regions";
    if (!pinPolicy.has(regions)) {
      throw new PinataException("No regions provided in pin policy");
    }

    if ((pinPolicy.getJSONArray(regions)).length() != 0) {
      JSONArray regionValues = pinPolicy.getJSONArray(regions);
      for (Object o : regionValues) {
        JSONObject region = (JSONObject) o;
        if (!region.has("id") || !(region.get("id") instanceof String)) {
          throw new PinataException("region id must be a string");
        }
        String desiredReplicationCount = "desiredReplicationCount";
        if (!region.has(desiredReplicationCount) || !(region
            .get(desiredReplicationCount) instanceof Integer)
            || region.getInt(desiredReplicationCount) == 0) {
          throw new PinataException("desiredReplicationCount must be an integer");
        }
      }
    }
  }

  public static void validatePinataOptions(JSONObject options) throws PinataException {
    String cidVersion = "cidVersion";
    if (options.has(cidVersion) && (!(options.get(cidVersion) instanceof Integer)
          || (options.getInt(cidVersion) != 0 && options.getInt(cidVersion) != 1))) {
      throw new PinataException("unsupported or invalid cidVersion");
    }

    String wrapWithDirectory = "wrapWithDirectory";
    if (options.has(wrapWithDirectory) && (!(options.get(wrapWithDirectory) instanceof Boolean)
          || (!options.getBoolean(wrapWithDirectory)
              && options.getBoolean(wrapWithDirectory)))) {
      throw new PinataException("wrapWithDirectory must be a boolean value of true or false");
    }

    if (options.has("hostNodes")) {
      validateHostNodes(options.get("hostNodes"));
    }

    if (options.has("customPinPolicy")) {
      validatePinPolicyStructure(options.getJSONObject("customPinPolicy"));
    }
  }

  public static void validateHostNodes(Object hostNodes) throws PinataException {
    if (!(hostNodes instanceof JSONArray)) {
      throw new PinataException("host_nodes value must be an array");
    }
    JSONArray nodes = (JSONArray) hostNodes;
    for (int i = 0; i < nodes.length(); i++) {
      String nodeValue = (nodes.get(i)).toString();
      if (!IsIpfs.isPeerMultiAddr(nodeValue)) {
        throw new PinataException(
            "host_node array entry: " + nodeValue + " is not a valid peer multiaddr");
      }
    }
  }

}
