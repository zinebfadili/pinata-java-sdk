package pinata;

/**
 * Pinata response object.
 */
public class PinataResponse {

  private int status;
  private String body;

  public PinataResponse() {

  }

  public PinataResponse(int status, String message) {
    this.status = status;
    this.body = message;
  }

  public int getStatus() {
    return status;
  }

  public String getBody() {
    return body;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public void setBody(String body) {
    this.body = body;
  }
}
