package com.example.security;

import akka.javasdk.JwtClaims;

import static akka.javasdk.http.HttpException.unauthorized;

/** Edge-only helper for extracting browser user identity from validated WorkOS JWT claims. */
public final class WorkosClaimExtractor {

  private WorkosClaimExtractor() {}

  public static WorkosUserIdentity from(JwtClaims claims) {
    var subject = claims.subject().orElseThrow(() -> unauthorized("JWT subject is required"));
    var email = firstPresent(claims, "email", "workos_email", "preferred_username");
    var displayName = firstPresent(claims, "name", "display_name");
    if (displayName.isBlank()) {
      var given = claims.getString("given_name").orElse("").trim();
      var family = claims.getString("family_name").orElse("").trim();
      displayName = (given + " " + family).trim();
    }
    if (displayName.isBlank()) {
      displayName = email;
    }
    return new WorkosUserIdentity(subject, email, displayName);
  }

  private static String firstPresent(JwtClaims claims, String... names) {
    for (var name : names) {
      var value = claims.getString(name).orElse("").trim();
      if (!value.isBlank()) {
        return value;
      }
    }
    return "";
  }
}
