package {{JAVA_BASE_PACKAGE}}.domain.security;

public record UserSettings(String accountId, UiMode uiMode) {
  public enum UiMode {
    LIGHT,
    DARK
  }
}
