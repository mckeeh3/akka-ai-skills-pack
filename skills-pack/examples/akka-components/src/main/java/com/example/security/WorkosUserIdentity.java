package com.example.security;

/** Minimal WorkOS identity extracted from validated JWT claims for local account linking. */
public record WorkosUserIdentity(String subject, String email, String displayName) {

  public WorkosUserIdentity {
    subject = normalize(subject);
    email = normalizeEmail(email);
    displayName = displayName == null ? "" : displayName.trim();
  }

  public boolean hasEmail() {
    return !email.isBlank();
  }

  private static String normalize(String value) {
    return value == null ? "" : value.trim();
  }

  private static String normalizeEmail(String value) {
    return value == null ? "" : value.trim().toLowerCase();
  }
}
