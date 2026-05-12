package com.example.security;

import com.example.domain.UserAccount;

public record AuthContext(
  String actorUserId,
  String effectiveUserId,
  String workosUserId,
  UserAccount actor,
  UserAccount effectiveUser,
  boolean impersonating
) {}
