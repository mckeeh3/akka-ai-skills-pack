# User Admin Workstream Expert Bundle

## Bundle identity

- bundle-id: `user-admin-agent.expertise`
- owning functional agent: `user-admin-agent`
- scope: foundation SaaS User Admin workstream for invitations, users, memberships, roles/capabilities, access review, support-access visibility, and admin audit in the selected `AuthContext`
- authoritative catalog link: `../functional-agents.md`
- primary surfaces: `user-admin-dashboard`, `user-admin-user-list`, `user-admin-user-account`, `decision-card`, `audit-trace-explorer`
- capability families:
  - `secure-tenant-user-foundation` for user, membership, role, invitation, support-access, access-review, and admin-audit operations
  - `managed-agent-foundation` for governed prompt/skill/reference/manifest/boundary loading and traces
  - `governance-decisions-audit` for approval-required decision cards and audit evidence
  - `frontend-shell-integration-patterns` for shell/context exposure and `/api/me` capability gating
- governance owner: Tenant Admin or delegated Agent Steward for content proposals; Reviewer/Approver or Policy Owner required for authority expansion; Auditor read-only where permitted

## Authority profile

The bundle guides User Admin work. It does not grant authority. Backend capability checks, selected `AuthContext`, policy gates, and `ToolPermissionBoundary` remain authoritative.

| Actor/context | Allowed agent posture | Required boundary |
|---|---|---|
| Tenant Admin in tenant context | Read scoped dashboard/list/detail/audit evidence; draft invitations and role recommendations; request human-confirmed admin mutations; route risky work to decision cards. | Tenant-scoped capability grants and policy checks. |
| Customer Admin in customer context | Read and propose only customer-scoped user/membership/invitation actions allowed by customer role policy. | No tenant-level role, support-access, or cross-customer operations. |
| SaaS Owner support role | Explain and summarize only when explicit support-access grant is active and audited. | No direct tenant data access without support grant. |
| Auditor | Read scoped audit/access-review evidence and trace explanations. | No mutation, draft-send, role change, or support-access expansion tools. |
| Disabled actor or inactive membership | Safe denial only. | No capability/tool calls except denial/audit where platform permits. |

Side-effecting actions default to proposal, human confirmation, or decision-card approval. The agent must not autonomously send invitations, grant roles, disable accounts, alter support access, resolve access reviews, relink identity, reset identity, or change policy.

## Model binding

This LLM-backed bundle uses an inherited governed default model binding unless a tenant-approved override is explicitly activated for `user-admin-agent`:

- inherited governed default model binding: `ModelConfigRef:foundation-user-admin-default-model` with `ModelPolicy:foundation-user-admin-model-policy`;
- allowed modes: runtime, test, replay, evaluation;
- fallback policy: no implicit fallback; approved fallback requires admin-governed policy and trace;
- provider secret boundary: the bundle, prompt, skills, references, manifests, traces, and browser surfaces may contain only safe provider/model aliases and never API keys, credential names, secret URLs, or deployment secret values;
- runtime requirement: resolve and validate the `ModelConfigRef`/`ModelPolicy` before model invocation, deny unknown/disabled/cross-scope/policy-denied bindings fail-closed, and record safe model refs plus policy/fallback decisions in `PromptAssemblyTrace` and `AgentWorkTrace`.

## Prompt intent

The active `PromptDocument`/`PromptVersion` for `user-admin-agent` instructs the model to:

- help administrators understand scoped User Admin state, allowed actions, denials, risks, and evidence;
- ask clarifying questions when tenant/customer context, target user, intended role, or approval path is ambiguous;
- prefer least-privilege recommendations and cite capability ids, policy constraints, audit evidence, and trace links;
- draft safe invitation rationale, access-review summaries, role recommendations, and decision-card facts;
- refuse raw invitation tokens, secrets, provider credentials, unredacted out-of-scope data, cross-tenant data, unsupported bulk side effects, role escalation, last-admin loss, and actions by disabled users;
- escalate risky or consequential actions to decision cards with evidence, alternatives, risk, confidence, and required approver scope.

## Governed procedural skill documents

These `SkillDocument` records are assigned through `AgentSkillManifest`. The compact manifest exposes ids, titles, short summaries, when-to-use hints, version policy, and authority notes; full skill text loads only through authorized `readSkill(skillId)`.

| skillId | Title | When to use | Authority note |
|---|---|---|---|
| `ua.access-review-triage.v1` | Access Review Triage | Evaluate stale memberships, risky roles, pending review items, and proposed remediation paths. | Recommendation/proposal only; resolving review items requires capability authorization and human confirmation/approval. |
| `ua.admin-risk-scoring.v1` | Admin Action Risk Scoring | Classify last-admin risk, role escalation, support-access expansion, identity relink/reset, bulk operations, and low-confidence recommendations. | Risk labels cannot block or allow actions by themselves; policy/capabilities decide. |
| `ua.invitation-drafting.v1` | Invitation Drafting | Draft onboarding rationale, least-privilege role explanation, delivery caveats, and resend/revoke explanations. | No raw token exposure; sending/resending/revoking requires human-confirmed capability call. |
| `ua.role-recommendation.v1` | Role Recommendation | Recommend roles/capabilities from job/context, memberships, policy, audit, and alternatives. | Cannot grant roles; role changes remain approval-gated or human-confirmed. |
| `ua.support-access-review.v1` | Support Access Review | Explain support-access grants, revocations, expiration, and SaaS Owner support limits. | No tenant data access unless support grant capability is active and audited. |
| `ua.audit-summary.v1` | Admin Audit Summary | Summarize scoped AdminAuditEvent and trace evidence for actions, denials, invitations, and role/membership changes. | Read-only; preserve redaction and do not infer hidden cross-scope facts. |

## Governed reference documents

These `ReferenceDocument` records are assigned through `AgentReferenceManifest`. The compact manifest exposes ids, titles, summaries, when-to-consult hints, version policy, and authority notes; full reference text loads only through authorized `readReferenceDoc(referenceId)`.

| referenceId | Title | When to consult | Authority note |
|---|---|---|---|
| `ua.tenant-role-catalog.v1` | Tenant Role and Capability Catalog | Explain role meanings, least-privilege alternatives, and capability ids shown in User Admin surfaces. | Descriptive only; backend role/capability checks are authoritative. |
| `ua.invitation-onboarding-policy.v1` | Invitation and Onboarding Policy | Draft invitation rationale, resend/revoke explanations, expiry behavior, and onboarding caveats. | Does not expose invitation tokens or email-provider secrets. |
| `ua.access-review-policy.v1` | Access Review Policy | Evaluate stale access, review cadence, resolver expectations, and escalation triggers. | Cannot mark reviews resolved without authorized capability call. |
| `ua.support-access-procedure.v1` | Support Access Operating Procedure | Explain SaaS Owner support access, customer/tenant visibility, expiry, and audit obligations. | Cannot create support access; requires explicit tenant-scoped support grant. |
| `ua.last-admin-protection.v1` | Last Admin Protection Rule | Explain why removal/disable/role downgrade may be blocked and how to recover safely. | Policy evidence only; command handlers enforce the invariant. |
| `ua.admin-audit-redaction-guide.v1` | Admin Audit Redaction Guide | Explain redaction markers, safe evidence summaries, and export limits. | Does not permit unredacted or cross-scope audit export. |

## Compact expertise manifest

Prompt assembly for `user-admin-agent` includes only compact manifest entries:

- assigned skill ids/titles/summaries/when-to-use hints from `AgentSkillManifest`;
- assigned reference ids/titles/summaries/when-to-consult hints from `AgentReferenceManifest`;
- active version policy, provenance/checksum summary, and authority notes;
- no full skill/reference bodies, no filesystem paths, no hidden seed-resource locations, and no model-supplied resource selectors.

The model may request a listed id through `readSkill(skillId)` or `readReferenceDoc(referenceId)`. The loader returns full content only after tenant/customer scope, active agent, active manifest assignment, document status, token/redaction limits, and `ToolPermissionBoundary` checks pass.

## Dashboard, surface graph, and governed-tool map

The expertise bundle must teach the functional agent to treat `user-admin-dashboard` as the role-specific dashboard trunk and `user-admin-user-list`, `user-admin-user-account`, `decision-card`, and `audit-trace-explorer` as graph branches. The agent should explain attention cards and action availability in terms of selected `AuthContext`, capability ids, qualified governed-tools, stale/forbidden states, trace ids, and denial categories.

Expired-invitation example: if the dashboard shows an expired invitation, the agent may explain expiry, open the filtered invitation queue, draft a resend/reinvite rationale, or route a risky role request to a decision card. It must not expose raw tokens, silently resend email, or infer cross-scope invitation existence.

| Capability / governed-tool group | Agent use | Qualified exposure | Boundary |
|---|---|---|---|
| `admin.users.dashboard.read`, `admin.users.search`, `admin.users.detail.read` | Read and explain dashboard/list/detail state. | agent-tool, browser-tool | Selected `AuthContext`, tenant/customer filter, redacted outputs, audit/trace correlation. |
| `admin.audit.read`, `admin.access_review.read`, `admin.support_access.read` | Summarize evidence and explain risks. | agent-tool, browser-tool | Read-only; preserve redaction and support-access scope. |
| `admin.invitations.create/resend/revoke` | Draft rationale and prepare human-confirmed action payloads. | browser-tool; human-confirmed agent-tool | No raw tokens; send/resend/revoke require explicit user confirmation and capability authorization. |
| `admin.memberships.*`, `admin.roles.*`, `admin.users.disable/reactivate`, `admin.support_access.grant/revoke/extend`, `admin.access_review.resolve` | Recommend, compare alternatives, and create decision-card facts for risky cases. | browser-tool, internal-tool, approval-gated workflow-tool | No autonomous side effects; last-admin, role escalation, support-access expansion, bulk operations, and low-confidence cases require decision-card approval. |
| `admin.users.profile.patch`, `admin.users.identity_relink.request/complete` | Explain and route exceptional identity/profile actions. | browser-tool, approval-gated workflow-tool | Human-confirmed or approval-gated; identity relink/reset is risky by default. |
| `readSkill(skillId)` | Load assigned active procedural skill text. | agent-tool | Requires `read_skill` grant, manifest assignment, active document/version, token/redaction checks, and `SkillLoadTrace`. |
| `readReferenceDoc(referenceId)` | Load assigned active reference text. | agent-tool | Requires `read_reference` grant, manifest assignment, active document/version, token/redaction checks, and `ReferenceLoadTrace`. |
| email/Resend preview/send governed-tools | Preview invitation email content where allowed; send only through invitation capability. | browser-tool; narrowly granted agent-tool | No provider secrets; send requires invitation capability authorization, idempotency, and audit. |

## Required denials and safe recovery

The agent must produce safe denial explanations without leaking out-of-scope data for:

- unassigned, inactive, disabled-agent, cross-tenant, wrong-customer, oversized, redaction-failed, or missing-boundary `readSkill` requests;
- unassigned, inactive, disabled-agent, cross-tenant, wrong-customer, oversized, redaction-failed, or missing-boundary `readReferenceDoc` requests;
- missing `read_skill` or `read_reference` grants in `ToolPermissionBoundary`;
- prompt, skill, reference, or manifest text that claims new roles, tenant scope, tool access, approval rights, or backend capabilities;
- Customer Admin tenant-level actions or cross-customer visibility;
- SaaS Owner support access without active support grant;
- disabled actor actions;
- role escalation, last-admin loss, unsupported bulk side effects, raw invitation token exposure, provider-secret access, and unredacted audit export.

Safe recovery should name the visible denial code/category, requested scope if safe, missing authority class, suggested non-sensitive next step, and trace/correlation id when available.

## Surfaces and visible evidence

- `user-admin-dashboard`: shows pending invitation/access-review/support-access/admin-audit summaries, safe agent recommendations, denial cards, and trace links.
- `user-admin-user-list`: shows scoped user rows, row action capability ids, role/membership status, invitation status, and safe action explanations.
- `user-admin-user-account`: shows detail evidence, membership/role/support/access-review/audit excerpts, last-admin warnings, and decision-card links.
- `decision-card`: renders risky action facts, evidence, confidence, alternatives, required approver scope, and approval/rejection actions.
- `audit-trace-explorer`: renders AdminAuditEvent, PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace, AgentWorkTrace, data-access, and denial traces with redaction.

## Trace requirements

Every User Admin agent turn must preserve correlation ids and selected `AuthContext` where applicable.

| Trace/audit record | Required contents |
|---|---|
| `PromptAssemblyTrace` | agent id, prompt document/version, compact skill manifest id, compact reference manifest id, tool boundary id, AuthContext, policy context, redaction marker, assembly checksum, correlation id. |
| `SkillLoadTrace` | requested skillId, allowed/denied result, skill document/version when allowed, manifest reason, boundary reason, AuthContext, redaction/token-limit result, correlation id. |
| `ReferenceLoadTrace` | requested referenceId, allowed/denied result, reference document/version when allowed, manifest reason, boundary reason, AuthContext, redaction/token-limit result, correlation id. |
| `AgentWorkTrace` | user intent, surface ids, capability ids, tool calls, data-access summaries, recommendations, denials, decision-card ids, audit event ids, prompt/load trace links, correlation id. |
| `AdminAuditEvent` | invitation, membership, role, account-status, support-access, access-review, behavior-document, manifest, boundary, loader allow/deny, and decision-card actions where consequential or policy-required. |

## Seed and upgrade policy

First-install or tenant-bootstrap seed import must create default active governed records for this bundle: `AgentDefinition`, inherited governed default `ModelConfigRef`/`ModelPolicy` binding, prompt v1, six `SkillDocument`/`SkillVersion` records, six `ReferenceDocument`/`ReferenceVersion` records, `AgentSkillManifest`, `AgentReferenceManifest`, and `ToolPermissionBoundary` with `read_skill` and `read_reference` grants. Imports must record provenance, content checksums, idempotency keys, seed bundle version, and audit events. App upgrades may add or propose new defaults but must not overwrite tenant-customized active versions without a governed review/activation path.

## Test obligations

Linked tests in `../../30-tests/test-index.md` must cover:

- compact expertise manifest assembly without full skill/reference bodies;
- assigned active `readSkill` and `readReferenceDoc` loads for User Admin documents;
- denied unassigned/inactive/cross-tenant/wrong-customer/oversized/redaction-failed/missing-boundary skill and reference loads;
- missing `read_skill` and missing `read_reference` tool-boundary denials;
- no authority expansion from prompt, skill, reference, or manifest text;
- capability authorization for dashboard read, user search/list, user detail, audit summary, invitation draft/send, role recommendation/change, support access, access review, and risky decision-card routing;
- Tenant Admin, Customer Admin, SaaS Owner support, Auditor, disabled-user, forbidden, redacted, empty, stale, and error surface states;
- model binding resolution/denial trace facts, provider secret non-exposure, and trace emission for `PromptAssemblyTrace`, `SkillLoadTrace`, `ReferenceLoadTrace`, `AgentWorkTrace`, data access, decision-card, and AdminAuditEvent records.
