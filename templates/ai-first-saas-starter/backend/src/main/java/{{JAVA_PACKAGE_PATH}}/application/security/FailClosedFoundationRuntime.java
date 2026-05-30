package {{JAVA_BASE_PACKAGE}}.application.security;

/** Shared fail-closed helpers for foundation ports that do not yet have durable Akka bindings. */
final class FailClosedFoundationRuntime {
  static final String LOCAL_DEMO_ENV = "AI_FIRST_SAAS_LOCAL_DEMO_REPOSITORIES";
  static final String MESSAGE = "Durable foundation repository binding is required for normal runtime. "
      + "Set " + LOCAL_DEMO_ENV + "=true only for explicit local/demo inspection, or bind an Akka-backed repository before external use.";

  private FailClosedFoundationRuntime() {}

  static boolean localDemoRepositoriesEnabled() {
    return "true".equalsIgnoreCase(System.getenv(LOCAL_DEMO_ENV));
  }

  static boolean testRuntime() {
    return System.getProperty("surefire.test.class.path") != null || System.getProperty("failsafe.test.class.path") != null;
  }

  static boolean localDemoOrTestEnabled() {
    return localDemoRepositoriesEnabled() || testRuntime();
  }

  static IllegalStateException unavailable(String portName) {
    return new IllegalStateException(portName + " unavailable: " + MESSAGE);
  }
}
