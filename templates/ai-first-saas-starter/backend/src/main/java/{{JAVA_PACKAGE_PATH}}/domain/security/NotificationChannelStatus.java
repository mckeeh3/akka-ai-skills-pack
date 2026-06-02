package {{JAVA_BASE_PACKAGE}}.domain.security;

/** Runtime availability of a governed notification channel. */
public enum NotificationChannelStatus {
  ACTIVE,
  LOCAL_TEST_CAPTURED,
  PROVIDER_UNCONFIGURED,
  DISABLED
}
