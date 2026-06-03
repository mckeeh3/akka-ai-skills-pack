package ai.first.application.security;

import akka.javasdk.ServiceSetup;
import akka.javasdk.annotations.Setup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Akka service startup hook for required foundation bootstrap.
 *
 * <p>Akka invokes one @Setup ServiceSetup implementation per service instance startup. Keep startup work
 * idempotent because multiple instances and rolling restarts may run this hook more than once.</p>
 */
@Setup
public final class StarterServiceSetup implements ServiceSetup {
  private static final Logger logger = LoggerFactory.getLogger(StarterServiceSetup.class);

  @Override
  public void onStartup() {
    logger.info("Running starter SaaS foundation startup bootstrap");
    StarterSecurityComponents.startup();
  }
}
