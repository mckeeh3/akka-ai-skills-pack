package ai.first.application.foundation.identity;

public final class AuthorizationException extends RuntimeException {
  private final int httpStatus;
  private final String reasonCode;

  public AuthorizationException(int httpStatus, String reasonCode) {
    super(reasonCode);
    this.httpStatus = httpStatus;
    this.reasonCode = reasonCode;
  }

  public int httpStatus() {
    return httpStatus;
  }

  public String reasonCode() {
    return reasonCode;
  }
}
