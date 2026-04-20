package com.example.api;

import akka.http.javadsl.model.HttpHeader;
import akka.javasdk.JsonSupport;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.Description;
import akka.javasdk.annotations.JWT;
import akka.javasdk.annotations.mcp.McpEndpoint;
import akka.javasdk.annotations.mcp.McpPrompt;
import akka.javasdk.annotations.mcp.McpTool;
import akka.javasdk.mcp.AbstractMcpEndpoint;
import java.util.Optional;

/**
 * MCP endpoint example for request-context access, class-level JWT validation, and tenant-aware prompts.
 */
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.INTERNET))
@JWT(
    validate = JWT.JwtMethodMode.BEARER_TOKEN,
    bearerTokenIssuers = "test-issuer",
    staticClaims = @JWT.StaticClaim(claim = "role", values = "support"))
@McpEndpoint(
    path = "/mcp/support",
    serverName = "support-operations",
    serverVersion = "1.0.0",
    instructions =
        "Use these prompts and tools only for authenticated support workflows. Read caller context before generating tenant-specific guidance.")
public class SecureSupportMcpEndpoint extends AbstractMcpEndpoint {

  public record CallerSummary(
      String issuer,
      String subject,
      String role,
      String tenant,
      boolean internetPrincipal,
      int headerCount) {}

  @McpTool(
      description =
          "Return authenticated caller context as compact JSON, including bearer-token claims and request headers useful for support workflows.")
  public String callerContext() {
    return JsonSupport.encodeToString(currentCallerSummary());
  }

  @McpPrompt(
      description =
          "Build a support-triage prompt that includes authenticated caller identity and tenant routing context")
  public String triagePrompt(
      @Description("The customer issue or symptom to investigate") String issue,
      @Description("The severity such as low, medium, or high") String severity) {
    var caller = currentCallerSummary();

    return """
        You are assisting tenant %s.
        The authenticated support agent is %s with role %s.
        Token issuer: %s
        Issue severity: %s

        Investigate this issue:
        %s

        Ask for the smallest next missing fact before proposing a fix.
        """
        .formatted(
            caller.tenant(),
            caller.subject(),
            caller.role(),
            caller.issuer(),
            severity,
            issue);
  }

  private CallerSummary currentCallerSummary() {
    var claims = requestContext().getJwtClaims();
    var tenant = requestContext().requestHeader("X-Tenant").map(HttpHeader::value).orElse("public");

    return new CallerSummary(
        claims.issuer().orElse("unknown"),
        claims.subject().orElse("anonymous"),
        claims.getString("role").orElse("unknown"),
        tenant,
        requestContext().getPrincipals().isInternet(),
        requestContext().allRequestHeaders().size());
  }
}
