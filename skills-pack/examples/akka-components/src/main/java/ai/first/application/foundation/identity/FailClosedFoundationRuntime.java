package ai.first.application.foundation.identity;

/** Shared fail-closed helpers for foundation ports that do not yet have durable Akka bindings. */
public final class FailClosedFoundationRuntime {
  public static final String MESSAGE = "Durable Akka foundation repository binding is required for normal runtime. "
      + "Test doubles are allowed only from test source; generated-app runtime must bind Akka-backed repositories before external use.";

  private FailClosedFoundationRuntime() {}

  public static boolean testRuntime() {
    return System.getProperty("surefire.test.class.path") != null || System.getProperty("failsafe.test.class.path") != null;
  }

  public static boolean localDemoOrTestEnabled() {
    return testRuntime();
  }

  public static IllegalStateException unavailable(String portName) {
    return new IllegalStateException(portName + " unavailable: " + MESSAGE);
  }
}
