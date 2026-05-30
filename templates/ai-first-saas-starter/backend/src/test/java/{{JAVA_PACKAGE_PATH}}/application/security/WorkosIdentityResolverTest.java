package {{JAVA_BASE_PACKAGE}}.application.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.JwtClaims;
import com.fasterxml.jackson.databind.JsonNode;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class WorkosIdentityResolverTest {
  private HttpServer server;

  @AfterEach
  void tearDown() {
    if (server != null) {
      server.stop(0);
      server = null;
    }
    System.clearProperty("WORKOS_API_KEY");
    System.clearProperty("WORKOS_API_BASE_URL");
    WorkosIdentityResolver.clearCacheForTests();
  }

  @Test
  void tokenWithSubAndEmailResolvesWithoutWorkosApiLookup() {
    System.setProperty("WORKOS_API_KEY", "sk_test_should_not_be_used");
    System.setProperty("WORKOS_API_BASE_URL", "http://127.0.0.1:1");

    var repository = new LocalDemoIdentityRepository();
    BootstrapAdminSeeder.seedConfiguredAdmins(repository, "admin@example.com:TENANT_ADMIN:tenant-starter");
    var meService = new MeService(new AuthContextResolver(repository));
    var identity = WorkosIdentityResolver.fromClaims(claims(Map.of("sub", "user-1", "email", "admin@example.com", "name", "Admin User")));
    var me = meService.me(identity, null, "corr-email-token");

    assertEquals("user-1", identity.subject());
    assertEquals("admin@example.com", me.account().email());
    assertEquals("active", me.account().status());
  }

  @Test
  void tokenWithSubButNoEmailFetchesWorkosProfileAndCachesIt() throws Exception {
    var requests = new AtomicInteger();
    server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    server.createContext("/user_management/users/user-2", exchange -> {
      requests.incrementAndGet();
      assertEquals("Bearer sk_test_resolver", exchange.getRequestHeaders().getFirst("Authorization"));
      var body = "{\"email\":\"resolved@example.com\",\"first_name\":\"Resolved\",\"last_name\":\"User\"}";
      exchange.getResponseHeaders().set("Content-Type", "application/json");
      exchange.sendResponseHeaders(200, body.getBytes().length);
      exchange.getResponseBody().write(body.getBytes());
      exchange.close();
    });
    server.start();
    System.setProperty("WORKOS_API_KEY", "sk_test_resolver");
    System.setProperty("WORKOS_API_BASE_URL", "http://127.0.0.1:" + server.getAddress().getPort());

    var repository = new LocalDemoIdentityRepository();
    BootstrapAdminSeeder.seedConfiguredAdmins(repository, "resolved@example.com:TENANT_ADMIN:tenant-starter");
    var meService = new MeService(new AuthContextResolver(repository));
    var first = WorkosIdentityResolver.fromClaims(claims(Map.of("sub", "user-2")));
    var me = meService.me(first, null, "corr-profile-token");
    var second = WorkosIdentityResolver.fromClaims(claims(Map.of("sub", "user-2")));

    assertEquals("resolved@example.com", first.email());
    assertEquals("Resolved User", first.displayName());
    assertEquals("resolved@example.com", me.account().email());
    assertEquals("resolved@example.com", second.email());
    assertEquals(1, requests.get());
  }

  @Test
  void tokenWithSubButNoEmailStaysSafeWhenProfileLookupFails() {
    System.setProperty("WORKOS_API_KEY", "sk_test_unavailable");
    System.setProperty("WORKOS_API_BASE_URL", "http://127.0.0.1:1");

    var identity = WorkosIdentityResolver.fromClaims(claims(Map.of("sub", "user-3")));

    assertEquals("user-3", identity.subject());
    assertNull(identity.email());
  }

  @Test
  void resolvedEmailDoesNotCreateLocalAuthorizationByItself() throws Exception {
    server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    server.createContext("/user_management/users/user-4", exchange -> {
      var body = "{\"email\":\"unknown@example.com\",\"first_name\":\"Unknown\",\"last_name\":\"User\"}";
      exchange.sendResponseHeaders(200, body.getBytes().length);
      exchange.getResponseBody().write(body.getBytes());
      exchange.close();
    });
    server.start();
    System.setProperty("WORKOS_API_KEY", "sk_test_resolver");
    System.setProperty("WORKOS_API_BASE_URL", "http://127.0.0.1:" + server.getAddress().getPort());
    var repository = new LocalDemoIdentityRepository();
    var resolver = new AuthContextResolver(repository);

    var identity = WorkosIdentityResolver.fromClaims(claims(Map.of("sub", "user-4")));
    var denied = org.junit.jupiter.api.Assertions.assertThrows(
        AuthorizationException.class,
        () -> resolver.resolveMe(identity, null, "corr-unknown"));

    assertEquals("no-local-account-or-invitation", denied.reasonCode());
  }

  @Test
  void secretBoundaryDoesNotExposeApiKeyInResolvedIdentity() throws Exception {
    server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    server.createContext("/user_management/users/user-5", exchange -> {
      var body = "{\"email\":\"safe@example.com\"}";
      exchange.sendResponseHeaders(200, body.getBytes().length);
      exchange.getResponseBody().write(body.getBytes());
      exchange.close();
    });
    server.start();
    System.setProperty("WORKOS_API_KEY", "sk_test_secret_boundary");
    System.setProperty("WORKOS_API_BASE_URL", "http://127.0.0.1:" + server.getAddress().getPort());

    var identity = WorkosIdentityResolver.fromClaims(claims(Map.of("sub", "user-5")));

    assertTrue(identity.toString().contains("safe@example.com"));
    assertTrue(!identity.toString().contains("sk_test_secret_boundary"));
  }

  private static JwtClaims claims(Map<String, String> values) {
    return new TestJwtClaims(values);
  }

  private record TestJwtClaims(Map<String, String> claims) implements JwtClaims {
    @Override public Iterable<String> allClaimNames() { return claims.keySet(); }
    @Override public Map<String, String> asMap() { return claims; }
    @Override public boolean hasClaims() { return !claims.isEmpty(); }
    @Override public Optional<String> issuer() { return Optional.ofNullable(claims.get("iss")); }
    @Override public Optional<String> subject() { return Optional.ofNullable(claims.get("sub")); }
    @Override public Optional<String> audience() { return Optional.ofNullable(claims.get("aud")); }
    @Override public Optional<Instant> expirationTime() { return Optional.empty(); }
    @Override public Optional<Instant> notBefore() { return Optional.empty(); }
    @Override public Optional<Instant> issuedAt() { return Optional.empty(); }
    @Override public Optional<String> jwtId() { return Optional.ofNullable(claims.get("jti")); }
    @Override public Optional<String> getString(String name) { return Optional.ofNullable(claims.get(name)); }
    @Override public Optional<Integer> getInteger(String name) { return Optional.ofNullable(claims.get(name)).map(Integer::valueOf); }
    @Override public Optional<Long> getLong(String name) { return Optional.ofNullable(claims.get(name)).map(Long::valueOf); }
    @Override public Optional<Double> getDouble(String name) { return Optional.ofNullable(claims.get(name)).map(Double::valueOf); }
    @Override public Optional<Boolean> getBoolean(String name) { return Optional.ofNullable(claims.get(name)).map(Boolean::valueOf); }
    @Override public Optional<Instant> getNumericDate(String name) { return Optional.empty(); }
    @Override public Optional<JsonNode> getObject(String name) { return Optional.empty(); }
    @Override public Optional<List<String>> getStringList(String name) { return Optional.empty(); }
    @Override public Optional<List<Integer>> getIntegerList(String name) { return Optional.empty(); }
    @Override public Optional<List<Long>> getLongList(String name) { return Optional.empty(); }
    @Override public Optional<List<Double>> getDoubleList(String name) { return Optional.empty(); }
    @Override public Optional<List<Boolean>> getBooleanList(String name) { return Optional.empty(); }
    @Override public Optional<List<Instant>> getNumericDateList(String name) { return Optional.empty(); }
    @Override public Optional<List<JsonNode>> getObjectList(String name) { return Optional.empty(); }
  }
}
