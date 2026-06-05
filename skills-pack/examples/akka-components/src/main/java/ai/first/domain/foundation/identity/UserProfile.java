package ai.first.domain.foundation.identity;

/** Browser-safe profile state. Profile fields never grant authorization. */
public record UserProfile(
    String accountId,
    String displayEmail,
    String displayName,
    String givenName,
    String familyName,
    String avatarUrl) {}
