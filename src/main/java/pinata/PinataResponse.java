package pinata;

/**
 * Pinata response object.
 */
public class PinataResponse {

  private int status;
  private String message;

  public PinataResponse() {

  }

  public PinataResponse(int status, String message) {
    this.status = status;
    this.message = message;
  }

  public int getStatus() {
    return status;
  }

  public String getMessage() {
    return message;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
