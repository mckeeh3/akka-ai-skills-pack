package {{JAVA_BASE_PACKAGE}}.domain.security;

/** Supported governed notification channels. External channels stay fail-closed until providers are configured. */
public enum NotificationChannel {
  IN_APP,
  EMAIL,
  WEBHOOK,
  SMS,
  MOBILE_PUSH,
  SLACK,
  TEAMS
}
