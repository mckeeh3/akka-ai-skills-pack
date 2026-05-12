package com.example.domain;

public record UserProfile(
  String firstName,
  String lastName,
  String displayName,
  String avatarUrl,
  String phone,
  String locale,
  String timezone
) {
  public static UserProfile empty() {
    return new UserProfile("", "", "", "", "", "en-US", "UTC");
  }
}
