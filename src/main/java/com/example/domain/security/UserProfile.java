package com.example.domain.security;

/** Browser-safe local profile fields for a signed-in user. */
public record UserProfile(String displayName, String email) {

  public static UserProfile empty() {
    return new UserProfile("", "");
  }
}
