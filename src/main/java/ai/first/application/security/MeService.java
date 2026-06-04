package ai.first.application.security;

import ai.first.domain.foundation.identity.WorkosIdentity;

/** Application service backing GET /api/me. */
public final class MeService {
  private final AuthContextResolver authContextResolver;
  private final MyAccountService myAccountService;

  public MeService(AuthContextResolver authContextResolver) {
    this(authContextResolver, new MyAccountService(authContextResolver));
  }

  public MeService(AuthContextResolver authContextResolver, MyAccountService myAccountService) {
    this.authContextResolver = authContextResolver;
    this.myAccountService = myAccountService;
  }

  public MeResponse me(WorkosIdentity identity, String selectedMembershipId, String correlationId) {
    var resolved = authContextResolver.resolveMe(identity, selectedMembershipId, correlationId);
    var summary = myAccountService.summary(resolved, correlationId);
    return MeResponse.from(
        resolved.account(),
        resolved.profile(),
        resolved.settings(),
        resolved.memberships(),
        resolved.selectedContext(),
        summary,
        resolved.correlationId());
  }
}
