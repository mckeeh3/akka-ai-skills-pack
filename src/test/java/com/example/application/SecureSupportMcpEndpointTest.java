package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.http.javadsl.model.HttpHeader;
import akka.http.javadsl.model.headers.RawHeader;
import akka.javasdk.JsonSupport;
import akka.javasdk.JwtClaims;
import akka.javasdk.Principal;
import akka.javasdk.Principals;
import akka.javasdk.Tracing;
import com.example.api.SecureSupportMcpEndpoint;
import com.fasterxml.jackson.databind.JsonNode;
import io.opentelemetry.api.trace.Span;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class SecureSupportMcpEndpointTest {

  @Test
  void callerContextToolReturnsJwtAndHeaderDetails() throws Exception {
    var endpoint = new SecureSupportMcpEndpoint();
    endpoint._internalSetRequestContext(
        new TestMcpRequestContext(
            List.of(RawHeader.create("X-Tenant", "acme"), RawHeader.create("X-Request-Id", "req-1")),
            Map.of("iss", "test-issuer", "sub", "alice", "role", "support"),
            true));

    var json = endpoint.callerContext();
    var caller =
        JsonSupport.getObjectMapper().readValue(json, SecureSupportMcpEndpoint.CallerSummary.class);

    assertEquals("test-issuer", caller.issuer());
    assertEquals("alice", caller.subject());
    assertEquals("support", caller.role());
    assertEquals("acme", caller.tenant());
    assertTrue(caller.internetPrincipal());
    assertEquals(2, caller.headerCount());
  }

  @Test
  void triagePromptIncludesCallerAndTenantContext() {
    var endpoint = new SecureSupportMcpEndpoint();
    endpoint._internalSetRequestContext(
        new TestMcpRequestContext(
            List.of(RawHeader.create("X-Tenant", "globex")),
            Map.of("iss", "test-issuer", "sub", "bob", "role", "support"),
            true));

    var prompt = endpoint.triagePrompt("Payment confirmation email missing", "high");

    assertTrue(prompt.contains("tenant globex"));
    assertTrue(prompt.contains("bob"));
    assertTrue(prompt.contains("Issue severity: high"));
    assertTrue(prompt.contains("Payment confirmation email missing"));
  }

  private record TestMcpRequestContext(
      List<HttpHeader> headers,
      Map<String, String> claims,
      boolean internetPrincipal)
      implements akka.javasdk.mcp.McpRequestContext {

    @Override
    public Principals getPrincipals() {
      return new TestPrincipals(headers, internetPrincipal);
    }

    @Override
    public JwtClaims getJwtClaims() {
      return new TestJwtClaims(claims);
    }

    @Override
    public Tracing tracing() {
      return new Tracing() {
        @Override
        public Optional<Span> startSpan(String name) {
          return Optional.empty();
        }

        @Override
        public Optional<Span> parentSpan() {
          return Optional.empty();
        }
      };
    }

    @Override
    public Optional<HttpHeader> requestHeader(String headerName) {
      return headers.stream().filter(header -> header.is(headerName.toLowerCase())).findFirst();
    }

    @Override
    public List<HttpHeader> allRequestHeaders() {
      return headers;
    }
  }

  private record TestPrincipals(List<HttpHeader> headers, boolean internetPrincipal) implements Principals {

    @Override
    public boolean isInternet() {
      return internetPrincipal;
    }

    @Override
    public boolean isSelf() {
      return false;
    }

    @Override
    public boolean isBackoffice() {
      return false;
    }

    @Override
    public boolean isLocalService(String name) {
      return false;
    }

    @Override
    public boolean isAnyLocalService() {
      return false;
    }

    @Override
    public Optional<String> getLocalService() {
      return Optional.empty();
    }

    @Override
    public Collection<Principal> get() {
      return internetPrincipal ? List.of(Principal.INTERNET) : List.of();
    }
  }

  private record TestJwtClaims(Map<String, String> claims) implements JwtClaims {

    @Override
    public Iterable<String> allClaimNames() {
      return claims.keySet();
    }

    @Override
    public Map<String, String> asMap() {
      return claims;
    }

    @Override
    public boolean hasClaims() {
      return !claims.isEmpty();
    }

    @Override
    public Optional<String> issuer() {
      return Optional.ofNullable(claims.get("iss"));
    }

    @Override
    public Optional<String> subject() {
      return Optional.ofNullable(claims.get("sub"));
    }

    @Override
    public Optional<String> audience() {
      return Optional.ofNullable(claims.get("aud"));
    }

    @Override
    public Optional<Instant> expirationTime() {
      return Optional.empty();
    }

    @Override
    public Optional<Instant> notBefore() {
      return Optional.empty();
    }

    @Override
    public Optional<Instant> issuedAt() {
      return Optional.empty();
    }

    @Override
    public Optional<String> jwtId() {
      return Optional.ofNullable(claims.get("jti"));
    }

    @Override
    public Optional<String> getString(String name) {
      return Optional.ofNullable(claims.get(name));
    }

    @Override
    public Optional<Integer> getInteger(String name) {
      return Optional.ofNullable(claims.get(name)).map(Integer::valueOf);
    }

    @Override
    public Optional<Long> getLong(String name) {
      return Optional.ofNullable(claims.get(name)).map(Long::valueOf);
    }

    @Override
    public Optional<Double> getDouble(String name) {
      return Optional.ofNullable(claims.get(name)).map(Double::valueOf);
    }

    @Override
    public Optional<Boolean> getBoolean(String name) {
      return Optional.ofNullable(claims.get(name)).map(Boolean::valueOf);
    }

    @Override
    public Optional<Instant> getNumericDate(String name) {
      return Optional.empty();
    }

    @Override
    public Optional<JsonNode> getObject(String name) {
      return Optional.empty();
    }

    @Override
    public Optional<List<String>> getStringList(String name) {
      return Optional.empty();
    }

    @Override
    public Optional<List<Integer>> getIntegerList(String name) {
      return Optional.empty();
    }

    @Override
    public Optional<List<Long>> getLongList(String name) {
      return Optional.empty();
    }

    @Override
    public Optional<List<Double>> getDoubleList(String name) {
      return Optional.empty();
    }

    @Override
    public Optional<List<Boolean>> getBooleanList(String name) {
      return Optional.empty();
    }

    @Override
    public Optional<List<Instant>> getNumericDateList(String name) {
      return Optional.empty();
    }

    @Override
    public Optional<List<JsonNode>> getObjectList(String name) {
      return Optional.empty();
    }
  }
}
