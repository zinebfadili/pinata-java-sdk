package util;

import java.io.IOException;
import java.util.Objects;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;
import pinata.PinataException;

public class RequestSender {

  private RequestSender() {
    throw new IllegalStateException("RequestSender class");
  }

  public static JSONObject getRequest(String endpoint, String pinataApiKey,
      String pinataSecretApiKey) throws PinataException, IOException {
    OkHttpClient client = new OkHttpClient().newBuilder().build();

    Request request = new Request.Builder()
        .url(endpoint)
        .addHeader("pinata_api_key", pinataApiKey)
        .addHeader("pinata_secret_api_key", pinataSecretApiKey)
        .build();

    Response response = client.newCall(request).execute();

    String responseBody = Objects.requireNonNull(response.body()).string();
    if (response.code() != 200) {
      throw new PinataException(
          "unknown server response while changing pin policy for user: " + responseBody);
    }

    JSONObject responseObject = new JSONObject();
    responseObject.put("body", responseBody);
    responseObject.put("status", response.code());
    return responseObject;
  }

  public static JSONObject postOrPutRequest(String method, String endpoint, RequestBody requestBody,
      String pinataApiKey, String pinataSecretApiKey) throws PinataException, IOException {
    OkHttpClient client = new OkHttpClient().newBuilder().build();

    Request request = new Request.Builder()
        .url(endpoint)
        .method(method, requestBody)
        .addHeader("pinata_api_key", pinataApiKey)
        .addHeader("pinata_secret_api_key", pinataSecretApiKey)
        .addHeader("Content-Type", "application/json")
        .build();

    Response response = client.newCall(request).execute();

    String responseBody = response.body().string();
    if (response.code() != 200) {
      throw new PinataException(
          "unknown server response while adding to pin queue: " + responseBody);
    }

    JSONObject responseObject = new JSONObject();
    responseObject.put("body", responseBody);
    responseObject.put("status", response.code());
    return responseObject;
  }

}
