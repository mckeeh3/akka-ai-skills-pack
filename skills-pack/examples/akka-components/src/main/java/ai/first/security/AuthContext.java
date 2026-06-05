package ai.first.security;

import ai.first.domain.security.LocalAccount;

/** Authenticated caller context derived from JWT claims plus local Akka account state. */
public record AuthContext(
    String actorUserId,
    String workosUserId,
    LocalAccount.State actor) {

  public boolean isActive() {
    return actor != null && actor.isActive();
  }
}
