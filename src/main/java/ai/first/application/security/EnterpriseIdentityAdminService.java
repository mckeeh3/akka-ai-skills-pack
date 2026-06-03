package ai.first.application.security;

import ai.first.domain.security.AdminAuditEvent;
import ai.first.domain.security.EnterpriseIdentityProviderStatus;
import ai.first.domain.security.FoundationRole;
import ai.first.domain.security.ScimProvisioningRequest;
import ai.first.domain.security.ScimProvisioningResult;
import ai.first.domain.security.ScopeType;
import ai.first.domain.security.SsoConfigurationValidation;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Provider-neutral IAM/SCIM/SSO admin foundation with fail-closed production seams. */
public final class EnterpriseIdentityAdminService {
  private static final List<String> REQUIRED_SECRET_NAMES = List.of("WORKOS_CLIENT_ID", "WORKOS_API_KEY", "WORKOS_REDIRECT_URI");

  private final IdentityRepository repository;
  private final Clock clock;
  private final Map<String, String> environment;

  public EnterpriseIdentityAdminService(IdentityRepository repository, Clock clock) {
    this(repository, clock, System.getenv());
  }

  public EnterpriseIdentityAdminService(IdentityRepository repository, Clock clock, Map<String, String> environment) {
    this.repository = repository;
    this.clock = clock;
    this.environment = environment == null ? Map.of() : Map.copyOf(environment);
  }

  public EnterpriseIdentityProviderStatus status(AuthContextResolver.ResolvedMe actor, String correlationId) {
    requireEnterpriseAdmin(actor, "IAM_STATUS_READ", correlationId);
    var configured = workosConfigured();
    audit(actor, "IAM_STATUS_READ", AdminAuditEvent.Result.ALLOWED, configured ? "provider-config-present" : "provider-config-missing", correlationId, null);
    return new EnterpriseIdentityProviderStatus(
        actor.selectedContext().tenantId(),
        actor.selectedContext().customerId(),
        true,
        true,
        configured,
        configured,
        configured ? "configured-provider-seam-ready-for-smoke-validation" : "fail-closed-until-workos-provider-secrets-are-configured",
        List.of(
            "WorkOS/AuthKit remains the supported browser authentication boundary.",
            "SCIM-style commands validate local authorization scope but do not claim production provisioning without configured provider secrets.",
            "No raw provider secrets, SCIM bearer tokens, SAML metadata secrets, or private provider ids are returned to the browser."),
        REQUIRED_SECRET_NAMES,
        trace("iam-status", correlationId));
  }

  public ScimProvisioningResult validateScimOperation(AuthContextResolver.ResolvedMe actor, ScimProvisioningRequest request, String correlationId) {
    requireEnterpriseAdmin(actor, "SCIM_OPERATION_VALIDATE", correlationId);
    if (request == null) throw new AuthorizationException(400, "validation:request");
    if (request.idempotencyKey() == null || request.idempotencyKey().isBlank()) throw new AuthorizationException(400, "idempotency-key-required");
    if (!List.of("CREATE_USER", "UPDATE_MEMBERSHIP", "DEACTIVATE_USER").contains(request.operation())) throw new AuthorizationException(400, "unsupported-scim-operation");
    if (request.email().isBlank()) throw new AuthorizationException(400, "validation:email");
    var scope = request.scopeType() == null ? actor.selectedContext().scopeType() : request.scopeType();
    requireScope(actor, scope, request.tenantId(), request.customerId());
    ensureNoRoleEscalation(actor, request.roles());
    var configured = workosConfigured();
    audit(actor, "SCIM_OPERATION_VALIDATE", configured ? AdminAuditEvent.Result.ALLOWED : AdminAuditEvent.Result.NO_OP, configured ? "provider-config-present" : "fail-closed-provider-config-missing", correlationId, request.email());
    return new ScimProvisioningResult(
        configured ? "validated-provider-ready" : "validated-local-only-fail-closed",
        configured
            ? "SCIM-style operation passed local authorization validation; run provider smoke validation before claiming production provisioning."
            : "SCIM-style operation passed local authorization validation, but production provider delivery is fail-closed because WorkOS provider secrets are not configured.",
        request.operation(),
        safeExternalId(request.externalId()),
        request.email(),
        scope,
        scope == ScopeType.SAAS_OWNER ? null : request.tenantId(),
        scope == ScopeType.CUSTOMER ? request.customerId() : null,
        request.roles(),
        true,
        configured,
        configured ? "provider-ready-not-sent-by-validation" : "fail-closed-provider-config-missing",
        trace("scim-validate", request.idempotencyKey()));
  }

  public SsoConfigurationValidation validateSsoConfiguration(AuthContextResolver.ResolvedMe actor, String domain, String issuer, String metadataUrl, boolean productionRequested, String correlationId) {
    requireEnterpriseAdmin(actor, "SSO_CONFIGURATION_VALIDATE", correlationId);
    if (domain == null || domain.isBlank()) throw new AuthorizationException(400, "validation:domain");
    if (issuer == null || issuer.isBlank()) throw new AuthorizationException(400, "validation:issuer");
    if (metadataUrl != null && metadataUrl.toLowerCase().contains("token=")) throw new AuthorizationException(400, "metadata-url-must-not-contain-secrets");
    var missing = REQUIRED_SECRET_NAMES.stream().filter(name -> environment.get(name) == null || environment.get(name).isBlank()).toList();
    var configured = missing.isEmpty();
    var status = productionRequested && !configured ? "production-fail-closed" : productionRequested ? "production-config-present" : "local-validation-only";
    audit(actor, "SSO_CONFIGURATION_VALIDATE", configured ? AdminAuditEvent.Result.ALLOWED : AdminAuditEvent.Result.NO_OP, status, correlationId, domain.toLowerCase());
    return new SsoConfigurationValidation(
        status,
        productionRequested && !configured
            ? "SSO provider metadata shape is locally valid, but production activation is blocked until WorkOS provider secrets are configured."
            : "SSO provider metadata shape is locally valid; WorkOS/AuthKit remains the browser authentication boundary.",
        actor.selectedContext().tenantId(),
        domain.toLowerCase(),
        issuer,
        productionRequested,
        configured,
        missing,
        trace("sso-validate", correlationId));
  }

  private void requireEnterpriseAdmin(AuthContextResolver.ResolvedMe actor, String action, String correlationId) {
    var scope = actor.selectedContext().scopeType();
    var capability = scope == ScopeType.SAAS_OWNER ? "saas_owner.user.manage" : scope == ScopeType.CUSTOMER ? "customer.user.manage" : "tenant.user.manage";
    if (!actor.selectedContext().hasCapability(capability)) {
      audit(actor, action, AdminAuditEvent.Result.DENIED, "missing-capability:" + capability, correlationId, null);
      throw new AuthorizationException(403, "missing-capability:" + capability);
    }
  }

  private void requireScope(AuthContextResolver.ResolvedMe actor, ScopeType scopeType, String tenantId, String customerId) {
    var auth = actor.selectedContext();
    if (scopeType == ScopeType.SAAS_OWNER && auth.scopeType() != ScopeType.SAAS_OWNER) throw new AuthorizationException(403, "scope-forbidden");
    if (scopeType == ScopeType.TENANT && (auth.scopeType() == ScopeType.SAAS_OWNER || !java.util.Objects.equals(auth.tenantId(), tenantId))) throw new AuthorizationException(403, "tenant-mismatch");
    if (scopeType == ScopeType.CUSTOMER && (!java.util.Objects.equals(auth.tenantId(), tenantId) || (auth.scopeType() == ScopeType.CUSTOMER && !java.util.Objects.equals(auth.customerId(), customerId)))) throw new AuthorizationException(403, "customer-mismatch");
  }

  private void ensureNoRoleEscalation(AuthContextResolver.ResolvedMe actor, List<FoundationRole> roles) {
    if (roles.contains(FoundationRole.SAAS_OWNER_ADMIN) && actor.selectedContext().scopeType() != ScopeType.SAAS_OWNER) throw new AuthorizationException(403, "role-escalation-denied");
    if (roles.stream().anyMatch(role -> role.defaultScopeType() == ScopeType.TENANT) && actor.selectedContext().scopeType() == ScopeType.CUSTOMER) throw new AuthorizationException(403, "role-escalation-denied");
  }

  private boolean workosConfigured() {
    return REQUIRED_SECRET_NAMES.stream().allMatch(name -> environment.get(name) != null && !environment.get(name).isBlank());
  }

  private String safeExternalId(String externalId) {
    if (externalId == null || externalId.isBlank()) return null;
    return externalId.length() <= 8 ? externalId : externalId.substring(0, 4) + "…" + externalId.substring(externalId.length() - 4);
  }

  private void audit(AuthContextResolver.ResolvedMe actor, String action, AdminAuditEvent.Result result, String reason, String correlationId, String targetUserId) {
    repository.appendAudit(new AdminAuditEvent(UUID.randomUUID().toString(), Instant.now(clock), correlationId, actor.account().accountId(), actor.selectedContext().membershipId(), actor.selectedContext().scopeType(), actor.selectedContext().tenantId(), actor.selectedContext().customerId(), targetUserId, null, action, result, reason, reason, "BROWSER_SAFE"));
  }

  private static String trace(String prefix, String value) {
    return "trace-" + prefix + "-" + Integer.toUnsignedString(java.util.Objects.requireNonNullElse(value, prefix).hashCode(), 36);
  }
}
