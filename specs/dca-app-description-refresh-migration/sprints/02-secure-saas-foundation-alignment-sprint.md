# Sprint 2: Secure SaaS Foundation Alignment

## Sprint goal

Make the mandatory secure SaaS foundation first-class in the DCA app description before DCA-specific lifecycle automation.

## Scope

- Add `10-capabilities/01-secure-tenant-user-foundation.md` for current foundation semantics.
- Align `40-auth-security/` with Account/Profile/Settings, Membership, Role, Permission/Capability, Invitation, AuthContext, `/api/me`, AdminAuditEvent, support access, billing boundary, and tenant/customer isolation.
- Clarify WorkOS/AuthKit authentication versus Akka-owned local authorization.
- Update implementation-slice notes if older bootstrap-only wording conflicts with current invite/onboarding requirements.

## Expected outputs

- First-class secure foundation capability.
- Auth/security files that link back to the capability and current doctrine.
- Explicit invitation/admin/support-access/billing-boundary semantics.

## Acceptance behavior

A future agent should not need to infer the DCA secure SaaS foundation from scattered notes or a PoC. The foundation should be explicit enough to block unsafe generation assumptions.

## Defer list

- Do not implement foundation code.
- Do not exhaustively define all DCA-specific business capabilities.
- Do not introduce self-registration or frontend-only authorization shortcuts.
