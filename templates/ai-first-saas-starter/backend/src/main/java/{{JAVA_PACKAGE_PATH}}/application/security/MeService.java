package {{JAVA_BASE_PACKAGE}}.application.security;

import {{JAVA_BASE_PACKAGE}}.domain.security.WorkosIdentity;

/** Application service backing GET /api/me. */
public final class MeService {
  private final AuthContextResolver authContextResolver;

  public MeService(AuthContextResolver authContextResolver) {
    this.authContextResolver = authContextResolver;
  }

  public MeResponse me(WorkosIdentity identity, String selectedMembershipId, String correlationId) {
    var resolved = authContextResolver.resolveMe(identity, selectedMembershipId, correlationId);
    return MeResponse.from(
        resolved.account(),
        resolved.profile(),
        resolved.settings(),
        resolved.memberships(),
        resolved.selectedContext(),
        resolved.correlationId());
  }
}
