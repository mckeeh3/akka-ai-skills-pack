package ai.first.domain.security;

/** Backend-owned in-app notification lifecycle state. */
public enum NotificationLifecycleStatus {
  UNREAD,
  READ,
  DISMISSED,
  ARCHIVED,
  SNOOZED,
  EXPIRED
}
