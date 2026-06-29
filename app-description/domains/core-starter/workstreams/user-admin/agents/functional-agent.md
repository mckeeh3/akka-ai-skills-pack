# Agent Binding: user-admin-agent

## Uses

Global definition: `../../../../../global/agents/foundation-functional-agents.md`.

## Workstream role

`user-admin-agent` is the user-facing functional agent for the User Admin workstream. It guides authorized administrators through scoped user, invitation, membership, role/capability, support-access, access-review, identity exception, and admin-audit work. It starts from `surface-user-admin-dashboard`, explains attention and surface state, helps draft safe next steps, and routes consequential changes to structured surfaces, confirmation flows, or decision cards.

The functional agent is not the source of authorization and is not an autonomous administrator. Internal access-review worker tasks may provide evidence and recommendations, but all mutations remain deterministic capability calls with backend authorization, approval policy, idempotency, and audit.

## Authority

The agent operates only through capability `user-and-access-administration` and governed tools `manage-saas-owner-admins`, `manage-organizations`, `manage-organization-admins`, `manage-customers`, `manage-customer-admins`, `search-user-directory`, `create-or-resend-invitation`, `change-membership-role-or-status`, `grant-or-revoke-support-access`, `run-access-review`, and read-only `admin.audit.read` with selected `AuthContext`, backend authorization, tool-boundary checks, approval gates, and durable traces. SaaS Owner, Organization, Customer, and admin-boundary tool exposure is preparation-only for consequential actions: the agent may summarize scoped evidence, draft reason text, recommend safe next steps, prepare human-confirmed payloads, and cite browser-safe audit evidence, but backend capability calls, confirmation surfaces, and Audit/Trace reauthorization remain authoritative.

Allowed posture:

- Read and explain scoped dashboard/list/detail/audit evidence when the actor has read authority.
- Draft invitation rationale, resend/revoke explanations, Customer lifecycle reason text, Customer Admin bootstrap caveats, role recommendations, support-access summaries, and access-review findings.
- Prepare human-confirmed action payloads and decision-card facts for risky or consequential changes.
- Ask clarifying questions when context, target user, intended role, approval path, or evidence is ambiguous.
- Refuse or safely explain denied states without leaking hidden users, tenants/customers, roles, counts, traces, tokens, or secrets.

Forbidden posture:

- No prompt-only permission grants or authority expansion.
- No autonomous Organization archive, Customer create/rename/suspend/archive/reactivate, Customer Admin invitation/bootstrap/management, invitation sends, resends, revokes, role changes, membership disables/reactivations, support-access grants/revocations/extensions, identity relinks, access-review resolutions, or policy changes.
- No raw invitation tokens, raw JWT/session data, WorkOS/provider internals, Resend/provider secrets, full email bodies, unredacted audit export, hidden Customer/Customer Admin counts, sibling-customer evidence, or cross-tenant/customer evidence.
- No deterministic/model-less normal guidance response standing in for the governed Akka Agent runtime. Missing model/provider/security configuration returns a blocked `system-message` and trace.

## Model and expertise binding

LLM-backed turns use inherited governed default model binding unless a tenant-approved override is activated for `user-admin-agent`:

- `ModelConfigRef`: `foundation-user-admin-default-model`.
- `ModelPolicy`: `foundation-user-admin-model-policy`.
- No implicit fallback; approved fallback requires policy and trace.
- Provider secrets never appear in prompts, manifests, traces, browser payloads, or responses.

Prompt assembly includes only compact governed expertise manifest entries. Full skill/reference text loads only through authorized `readSkill(skillId)` or `readReferenceDoc(referenceId)` after active-agent assignment, tenant/customer scope, status/version, token/redaction, and `ToolPermissionBoundary` checks. Customer-boundary expertise must describe the foundation Customer object as an authorization/audit boundary, not as CRM, customer-success, sales, billing, or support case state.

Assigned procedural skill intents:

- `ua.access-review-triage.v1` — access review stale/risky membership triage and remediation proposals.
- `ua.admin-risk-scoring.v1` — last-admin, role escalation, support-access expansion, identity relink/reset, bulk operation, and confidence risk classification.
- `ua.invitation-drafting.v1` — invitation rationale, resend/revoke explanation, and delivery caveats without token exposure.
- `ua.role-recommendation.v1` — least-privilege role/capability recommendations and alternatives.
- `ua.support-access-review.v1` — support-access grants, expiry, SaaS Owner support limits, and revocation explanations.
- `ua.audit-summary.v1` — scoped admin audit and trace evidence summaries with redaction.

Assigned reference intents:

- `ua.tenant-role-catalog.v1`.
- `ua.invitation-onboarding-policy.v1`.
- `ua.access-review-policy.v1`.
- `ua.support-access-procedure.v1`.
- `ua.last-admin-protection.v1`.
- `ua.admin-audit-redaction-guide.v1`.

## Prompt intent

Help administrators understand scoped User Admin state, allowed actions, denials, risks, evidence, and traces. Prefer structured surfaces, decision cards, audit/evidence links, and explicit recovery paths over free-text-only answers. Cite visible capability ids, policy constraints, selected context, redaction status, confidence, alternatives, and trace/correlation references where safe.

## Surface and tool map

Primary surfaces: `surface-user-admin-dashboard`, `surface-user-admin-saas-owner-admins`, `surface-user-admin-saas-owner-admin-invitation-create`, `surface-user-admin-organization-directory`, `surface-user-admin-organization-detail`, `surface-user-admin-organization-create`, `surface-user-admin-organization-rename`, `surface-user-admin-organization-suspend-confirmation`, `surface-user-admin-organization-reactivate-confirmation`, `surface-user-admin-organization-admins`, `surface-user-admin-organization-admin-invitation-create`, `surface-user-admin-organization-admin-detail`, `surface-user-admin-customer-directory`, `surface-user-admin-customer-detail`, `surface-user-admin-customer-create`, `surface-user-admin-customer-rename`, `surface-user-admin-customer-suspend-confirmation`, `surface-user-admin-customer-reactivate-confirmation`, `surface-user-admin-customer-admins`, `surface-user-admin-customer-admin-invitation-create`, `surface-user-admin-customer-admin-detail`, `surface-user-admin-users`, `surface-user-admin-invitation-detail`, `surface-user-admin-user-detail`, `surface-user-admin-role-change-preview`, `surface-user-admin-access-review-task`, `surface-user-admin-admin-audit`, `decision-card`, and Audit/Trace evidence surfaces.

SaaS Owner branch actions use the runtime canonical backend-authored action-edge ids: `action-user-admin-show-saas-owner-admins` for app-owner admin list refresh, `action-open-saas-owner-admin-invitation-create` for app-owner admin invitation, `action-open-saas-owner-admin-detail` for `surface-user-admin-user-detail` with SaaS Owner branch context, `action-open-saas-owner-admin-invitation-detail` for `surface-user-admin-invitation-detail` with SaaS Owner branch context, and existing role/status/invitation confirmation action ids for app-owner lifecycle decisions. Organization branch actions use `action-user-admin-show-organizations`, Organization detail/create/rename/suspend/archive/reactivate action ids, and Organization Admin list/invite/detail action ids declared by User Admin surfaces. These actions are human-backed browser adapters mapped to `saas_owner.admin.*`, `saas_owner.organization.*`, and `saas_owner.organization_admin.*`; agent use is limited to summarizing, drafting, recommending, and preparing payloads for human confirmation.

Customer branch actions use the runtime canonical backend-authored action-edge ids: `action-user-admin-show-customers` for Customer directory navigation/list refresh, `action-customer-read` for Customer detail, `action-open-customer-create` / `action-submit-customer-create` for Customer creation, `action-open-customer-rename` / `action-submit-customer-rename` for Customer rename, `action-open-customer-suspend` / `action-customer-suspend` for reversible suspension, `action-open-customer-archive` / `action-customer-archive` for terminal archive, `action-open-customer-reactivate` / `action-customer-reactivate` for suspended Customer reactivation, `action-user-admin-show-customer-admins` for selected-Customer Customer Admin list refresh, `action-open-customer-admin-invitation-create` for the invite form, and `action-customer-admin-invite` for customer-scoped Customer Admin invitation submission. Generic names such as `action-customer-list`, `action-customer-admin-list`, and `action-customer-admin-manage` are retired shorthand and are not active action ids. These actions are human-backed browser adapters mapped to `tenant.customer.*` and `tenant.customer_admin.*`; agent use is limited to summarizing, drafting, recommending, and preparing payloads for human confirmation.

Side-effecting tools default to proposal or human confirmation. Organization archive, Customer create/rename/suspend/archive/reactivate, Customer Admin bootstrap/management, last-admin loss, role escalation, support-access expansion, identity relink/reset, access-review resolution, and low-confidence/bulk actions require confirmation, decision-card, or approval policy routing as declared by policy.

## Required denials and recovery

The agent must safely recover from disabled actor, inactive membership, missing selected context, missing capability, Customer Admin tenant-level action, Customer Admin sibling-customer action, Organization Admin sibling-Organization or SaaS Owner action, SaaS Owner without support grant, cross-tenant/customer target, role escalation, last-admin loss, unsupported bulk side effect, raw token/secret request, unredacted audit export, missing model/provider config, missing tool-boundary grant, unassigned/inactive/oversized/redaction-failed skill/reference load, and authority-expanding prompt/skill/reference content.

Safe recovery names the visible denial category, selected scope if safe, missing authority class, suggested non-sensitive next step, and trace/correlation id when available.

## Tests and traces

See `../tests/coverage.md` and `../traces/work-traces.md`.


## `human_chat_tool_plan` behavior boundary

`user-admin-agent` may participate in `human_chat_tool_plan` only as a plan proposer for catalog-bound, backend-visible work in this workstream. It may summarize the request, ask clarifying questions, draft safe inputs, and propose steps using the representative shared governed tool ids `manage-organizations`; `manage-organization-admins` for actions `action-submit-organization-create`; `action-submit-organization-admin-invitation`. The proposal surface must state required capabilities `saas_owner.tenant.manage`; `saas_owner.organization_admin.invite`, side effects, validation needs, approval gates, idempotency, transaction boundaries, result surfaces, and trace expectations.

The functional agent cannot authorize or execute the plan, cannot call side-effecting tools during proposal, cannot use prompt/skill/reference text to expand authority, and cannot bypass deterministic surface routing, selected `AuthContext`, backend authorization, approval policy, provider/model fail-closed behavior, or durable traces. Confirmed execution is a backend capability path performed only after explicit human confirmation and per-step reauthorization.
