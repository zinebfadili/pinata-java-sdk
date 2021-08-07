package util.querybuilder;

import org.json.JSONObject;
import pinata.PinataException;

public interface QueryBuilder {

  String buildUrl(String baseUrl, JSONObject filters) throws PinataException;
}
