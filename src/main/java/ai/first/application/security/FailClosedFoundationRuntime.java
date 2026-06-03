package ai.first.application.security;

/** Shared fail-closed helpers for foundation ports that do not yet have durable Akka bindings. */
final class FailClosedFoundationRuntime {
  static final String MESSAGE = "Durable Akka foundation repository binding is required for normal runtime. "
      + "Test doubles are allowed only from test source; generated-app runtime must bind Akka-backed repositories before external use.";

  private FailClosedFoundationRuntime() {}

  static boolean testRuntime() {
    return System.getProperty("surefire.test.class.path") != null || System.getProperty("failsafe.test.class.path") != null;
  }

  static boolean localDemoOrTestEnabled() {
    return testRuntime();
  }

  static IllegalStateException unavailable(String portName) {
    return new IllegalStateException(portName + " unavailable: " + MESSAGE);
  }
}
