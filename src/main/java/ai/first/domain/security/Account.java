package ai.first.domain.security;

/** Local Akka-owned account linked to a WorkOS-authenticated human. */
public record Account(
    String accountId,
    String workosUserId,
    String normalizedEmail,
    String displayEmail,
    AccountStatus status,
    String identityLinkState) {

  public boolean active() {
    return status == AccountStatus.ACTIVE;
  }
}
