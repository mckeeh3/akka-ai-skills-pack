package {{JAVA_BASE_PACKAGE}}.domain.security;

public record UserSettings(String accountId, ThemeId themeId) {
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
