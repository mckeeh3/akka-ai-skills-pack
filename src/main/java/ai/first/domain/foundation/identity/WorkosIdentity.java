package ai.first.domain.foundation.identity;

public record WorkosIdentity(String subject, String email, String displayName) {
  public String normalizedEmail() {
    return email == null ? null : email.trim().toLowerCase();
  }
}
