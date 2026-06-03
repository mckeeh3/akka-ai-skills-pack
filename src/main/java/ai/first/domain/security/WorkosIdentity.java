package ai.first.domain.security;

public record WorkosIdentity(String subject, String email, String displayName) {
  public String normalizedEmail() {
    return email == null ? null : email.trim().toLowerCase();
  }
}
