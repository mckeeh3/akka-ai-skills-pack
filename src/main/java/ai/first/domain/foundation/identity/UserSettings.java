package ai.first.domain.foundation.identity;

public record UserSettings(String accountId, ThemeId themeId, String locale, String timeZone) {
  public static final String DEFAULT_LOCALE = "en-US";
  public static final String DEFAULT_TIME_ZONE = "America/New_York";

  public UserSettings(String accountId, ThemeId themeId) {
    this(accountId, themeId, DEFAULT_LOCALE, DEFAULT_TIME_ZONE);
  }

  public UserSettings {
    locale = normalizeLocale(locale);
    timeZone = normalizeTimeZone(timeZone);
  }

  public static String normalizeLocale(String locale) {
    if (locale == null || locale.isBlank()) return DEFAULT_LOCALE;
    var normalized = locale.trim();
    return switch (normalized) {
      case "en-US", "en-GB", "fr-FR", "es-ES" -> normalized;
      default -> throw new IllegalArgumentException("unknown locale: " + locale);
    };
  }

  public static String normalizeTimeZone(String timeZone) {
    if (timeZone == null || timeZone.isBlank()) return DEFAULT_TIME_ZONE;
    var normalized = timeZone.trim();
    return switch (normalized) {
      case "America/New_York", "America/Chicago", "America/Denver", "America/Los_Angeles", "Europe/London", "Europe/Paris", "UTC" -> normalized;
      default -> throw new IllegalArgumentException("unknown time zone: " + timeZone);
    };
  }
  public enum ThemeId {
    AURORA_LIGHT("aurora-light"),
    COBALT_LIGHT("cobalt-light"),
    OBSIDIAN_DARK("obsidian-dark"),
    MIDNIGHT_DARK("midnight-dark"),
    DARK_NIGHT("dark-night");

    private final String id;

    ThemeId(String id) {
      this.id = id;
    }

    public String id() {
      return id;
    }

    public static ThemeId fromId(String id) {
      return switch (id == null ? "" : id.trim().toLowerCase()) {
        case "aurora-light" -> AURORA_LIGHT;
        case "cobalt-light" -> COBALT_LIGHT;
        case "obsidian-dark" -> OBSIDIAN_DARK;
        case "midnight-dark" -> MIDNIGHT_DARK;
        case "dark-night" -> DARK_NIGHT;
        default -> throw new IllegalArgumentException("unknown theme id: " + id);
      };
    }
  }
}
