package ai.first.api.coreapp.workstream;

import ai.first.domain.foundation.identity.Membership;
import akka.http.javadsl.model.HttpResponse;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.JWT;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.client.ComponentClient;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.annotations.http.Post;
import akka.javasdk.http.AbstractHttpEndpoint;
import akka.javasdk.http.HttpResponses;
import static akka.javasdk.http.HttpException.forbidden;
import static akka.javasdk.http.HttpException.notFound;
import static akka.javasdk.http.HttpException.unauthorized;
import ai.first.application.foundation.workstream.AkkaWorkstreamLogRepository;
import ai.first.application.foundation.workstream.WorkstreamEventBackboneView;
import ai.first.application.foundation.workstream.WorkstreamEventPublisher;
import ai.first.application.foundation.identity.AuthorizationException;
import ai.first.application.foundation.invitation.InvitationService.AcceptInvitationRequest;
import ai.first.application.foundation.identity.StarterSecurityComponents;
import ai.first.application.foundation.identity.WorkosIdentityResolver;
import ai.first.application.coreapp.workstream.WorkstreamService;
import ai.first.application.coreapp.workstream.WorkstreamService.CapabilityActionRequest;
import ai.first.application.coreapp.workstream.WorkstreamService.ChatToolPlanConfirmationRequest;
import ai.first.application.coreapp.workstream.WorkstreamService.WorkstreamMessageRequest;
import ai.first.application.coreapp.workstream.WorkstreamService.WorkstreamShellRequest;
import ai.first.domain.foundation.identity.WorkosIdentity;
import ai.first.application.foundation.invitation.InvitationService;
import java.util.List;
import java.util.Map;

/** Workstream shell API endpoints for User Admin structured surfaces and capability actions. */
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.INTERNET))
@JWT(validate = JWT.JwtMethodMode.BEARER_TOKEN)
@HttpEndpoint("/api/workstream")
public class WorkstreamEndpoint extends AbstractHttpEndpoint {
  private final ComponentClient componentClient;
  private final WorkstreamService workstreamService;

  public WorkstreamEndpoint(ComponentClient componentClient) {
    this.componentClient = componentClient;
    this.workstreamService = StarterSecurityComponents.workstreamService(componentClient, new AkkaWorkstreamLogRepository(componentClient));
  }

  @Get("/bootstrap")
  public HttpResponse bootstrap() {
    return authorized((identity, selectedContextId, correlationId) -> HttpResponses.ok(workstreamService.bootstrap(identity, selectedContextId, correlationId)));
  }

  @Get("/functional-agents")
  public HttpResponse functionalAgents() {
    return authorized((identity, selectedContextId, correlationId) -> HttpResponses.ok(workstreamService.functionalAgents(identity, selectedContextId, correlationId)));
  }

  @Get("/items")
  public HttpResponse items() {
    var functionalAgentId = requestContext().queryParams().getString("functionalAgentId").orElse(null);
    return authorized((identity, selectedContextId, correlationId) -> HttpResponses.ok(workstreamService.items(identity, selectedContextId, functionalAgentId, correlationId)));
  }

  @Get("/surfaces/{surfaceId}")
  public HttpResponse surface(String surfaceId) {
    return authorized((identity, selectedContextId, correlationId) -> HttpResponses.ok(workstreamService.surface(identity, selectedContextId, surfaceId, correlationId)));
  }

  @Post("/actions")
  public HttpResponse action(CapabilityActionRequest request) {
    return authorized((identity, selectedContextId, correlationId) -> HttpResponses.ok(workstreamService.runAction(identity, selectedContextId, request)));
  }

  @Post("/shell-requests")
  public HttpResponse shellRequest(WorkstreamShellRequest request) {
    return authorized((identity, selectedContextId, correlationId) -> HttpResponses.ok(workstreamService.runShellRequest(identity, selectedContextId, request)));
  }

  @Post("/messages")
  public HttpResponse message(WorkstreamMessageRequest request) {
    return authorized((identity, selectedContextId, correlationId) -> HttpResponses.ok(workstreamService.submitMessage(identity, selectedContextId, request, correlationId)));
  }

  @Post("/chat-tool-plans/confirm")
  public HttpResponse confirmChatToolPlan(ChatToolPlanConfirmationRequest request) {
    return authorized((identity, selectedContextId, correlationId) -> HttpResponses.ok(workstreamService.confirmChatToolPlan(identity, selectedContextId, request)));
  }

  @Post("/invitations/accept")
  public HttpResponse acceptInvitation(AcceptInvitationRequest request) {
    return authorizedIdentityOnly((identity, correlationId) -> HttpResponses.ok(StarterSecurityComponents.invitationService().acceptForBrowser(identity, request, correlationId)));
  }

  /**
   * Long-running SSE stream for authorized workstream refresh hints.
   *
   * <p>The stream is backed by an Akka View query with {@code streamUpdates = true}; after the initial
   * matching result set it remains open and emits later matching projection-refresh rows until the
   * browser disconnects.
   */
  @Get("/events")
  public HttpResponse events() {
    var functionalAgentId = requestContext().queryParams().getString("functionalAgentId").orElse(null);
    return authorized((identity, selectedContextId, correlationId) -> {
      var actor = StarterSecurityComponents.authContextResolver().resolveMe(identity, selectedContextId, correlationId);
      var tenantId = viewQueryTenantId(actor.selectedContext().tenantId());
      var customerId = actor.selectedContext().customerId() == null ? "" : actor.selectedContext().customerId();
      var lastSeen = requestContext().lastSeenSseEventId()
          .or(() -> requestContext().queryParams().getString("lastEventId"))
          .map(java.time.Instant::parse);
      var source = functionalAgentId == null || functionalAgentId.isBlank()
          ? componentClient.forView().stream(WorkstreamEventBackboneView::streamContextEvents)
              .entriesSource(new WorkstreamEventBackboneView.ContextQuery(tenantId, customerId), lastSeen)
          : componentClient.forView().stream(WorkstreamEventBackboneView::streamFunctionalAgentEvents)
              .entriesSource(new WorkstreamEventBackboneView.FunctionalAgentQuery(tenantId, customerId, functionalAgentId), lastSeen);
      return HttpResponses.serverSentEventsForView(source);
    });
  }

  private HttpResponse authorized(AuthorizedCall call) {
    try {
      var identity = WorkosIdentityResolver.fromClaims(requestContext().getJwtClaims());
      var selectedContextId = requestContext().requestHeader("X-Selected-Context-Id")
          .or(() -> requestContext().requestHeader("X-Selected-Membership-Id"))
          .map(header -> header.value())
          .or(() -> requestContext().queryParams().getString("selectedContextId"))
          .orElse(null);
      var correlationId = correlationId();
      return call.invoke(identity, selectedContextId, correlationId);
    } catch (AuthorizationException error) {
      if (error.httpStatus() == 401) throw unauthorized(error.reasonCode());
      if ("account-disabled".equals(error.reasonCode()) || "no-active-membership".equals(error.reasonCode())) {
        return HttpResponses.ok(noAccessRecovery(correlationId(), error.reasonCode()));
      }
      if (error.httpStatus() == 404) throw notFound();
      throw forbidden(error.reasonCode());
    }
  }

  private Map<String, Object> noAccessRecovery(String correlationId, String reasonCode) {
    var data = Map.ofEntries(
        Map.entry("surfaceContract", "my_account.open_denied.v1"),
        Map.entry("decision", "account-disabled".equals(reasonCode) ? "disabled_account" : "inactive_membership"),
        Map.entry("safeReasonCode", reasonCode),
        Map.entry("severity", "warning"),
        Map.entry("title", "My Account recovery"),
        Map.entry("message", "Your signed-in account was recognized, but no active application context is available. My Account can only show safe profile/session recovery guidance until an administrator restores access."),
        Map.entry("accountContext", Map.of("selectedContextId", "not_available", "redactionLevel", "browser-safe")),
        Map.entry("recoverySteps", List.of("Sign out and try another account if appropriate.", "Contact an administrator to restore an active membership or account state.")),
        Map.entry("traceRefs", List.of("trace-workstream-no-access-recovery")),
        Map.entry("redaction", "No tenant, customer, membership, workstream, capability, provider, or hidden context details are enumerated."),
        Map.entry("noEnumeration", true));
    var surface = Map.ofEntries(
        Map.entry("surfaceId", "surface-my-account-open-denied"),
        Map.entry("surfaceType", "system_message"),
        Map.entry("surfaceVersion", "v1"),
        Map.entry("title", "Account access unavailable"),
        Map.entry("ownerFunctionalAgentId", "my-account-agent"),
        Map.entry("correlationId", correlationId),
        Map.entry("traceIds", List.of("trace-workstream-no-access-recovery")),
        Map.entry("data", data));
    return Map.ofEntries(
        Map.entry("status", "no_access_recovery"),
        Map.entry("reasonCode", reasonCode),
        Map.entry("correlationId", correlationId),
        Map.entry("surface", surface),
        Map.entry("surfaces", List.of(surface)),
        Map.entry("items", List.of()),
        Map.entry("functionalAgents", List.of()),
        Map.entry("availableActions", List.of("sign_out", "contact_admin")));
  }

  private HttpResponse authorizedIdentityOnly(IdentityOnlyCall call) {
    try {
      var identity = WorkosIdentityResolver.fromClaims(requestContext().getJwtClaims());
      return call.invoke(identity, correlationId());
    } catch (AuthorizationException error) {
      if (error.httpStatus() == 401) throw unauthorized(error.reasonCode());
      if (error.httpStatus() == 404) throw notFound();
      throw forbidden(error.reasonCode());
    }
  }

  private String correlationId() {
    return requestContext().requestHeader("X-Correlation-Id").map(header -> header.value()).orElse("api-workstream");
  }

  private static String viewQueryTenantId(String tenantId) {
    return tenantId == null || tenantId.isBlank() ? WorkstreamEventPublisher.PLATFORM_SCOPE_TENANT_ID : tenantId;
  }

  private interface AuthorizedCall {
    HttpResponse invoke(WorkosIdentity identity, String selectedContextId, String correlationId);
  }

  private interface IdentityOnlyCall {
    HttpResponse invoke(WorkosIdentity identity, String correlationId);
  }
}
