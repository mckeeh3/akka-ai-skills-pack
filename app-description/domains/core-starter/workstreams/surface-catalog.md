# Core starter surface catalog

Purpose: this catalog gives the deterministic surface intent router and governed agent guidance a shared, authority-neutral description of the current core workstream surfaces. It does **not** grant capabilities, submit actions, bypass selected `AuthContext`, or allow agents to perform side effects. Backend authorization, idempotency, audit/work traces, and protected surface/action handlers remain authoritative.

Catalog entry fields:

- **Surface id/title**: backend/frontend surface identifier and user-facing title.
- **Purpose**: what the surface helps the human inspect, decide, or prepare.
- **Prompt examples**: high-confidence examples that may open or prefill the surface.
- **Required capabilities**: descriptive capability hints that must still be checked by the protected backend path.
- **Prefill fields**: browser-safe draft fields the router may pass for review; empty means open-only.
- **Forbidden direct effects**: mutations or privileged reads the catalog must never imply.

## Router/catalog mapping

Current code-owned routing lives in `DefaultSurfaceIntentRouter` and should stay a side-effect-free consumer of this catalog until a typed code catalog is introduced. The current mappings derived from the catalog are:

| Route id | Workstream | Matched examples | Target surface | Required capability hint | Prefill fields | Notes |
|---|---|---|---|---|---|---|
| `route-my-account-dashboard-open-v1` | My Account | `open my account`, `show my account dashboard` | `surface-my-account-dashboard` | selected-context visibility for `my-account-agent` | none | Open-only; no capability or context grant. |
| `route-user-admin-organization-create-v1` | User Admin | `create organization "Org 1"`, `add organization "Org 1"` | `surface-user-admin-organization-create` | `saas_owner.tenant.manage` | `organizationName` | Opens create form with review-required prefill; no Organization is created. |
| `route-user-admin-organization-directory-v1` | User Admin | `show organizations`, `organization directory` | `surface-user-admin-organization-directory` | `saas_owner.tenant.read` | none | Open-only Organization discovery. |
| `route-user-admin-user-directory-v1` | User Admin | `show users`, `user directory` | `surface-user-admin-users` | `user_admin.list_members` or equivalent User Admin visibility | none | Open-only scoped user/invitation discovery. |
| `route-user-admin-invitation-create-v1` | User Admin | `invite user alex@example.com` | `surface-user-admin-invitation-create` | `user_admin.invite_user` | `email` | Opens invitation form with review-required prefill; no invitation is sent. |
| `route-agent-admin-dashboard-open-v1` | Agent Admin | `open agent admin dashboard`, `show agent admin dashboard` | `surface-agent-admin-dashboard` | `agent-doc-administration` | none | Open-only optional dashboard; no edit is started. |
| `route-agent-admin-agent-list-open-v1` | Agent Admin | `show agents`, `list agents`, `show agent list` | `surface-agent-admin-agent-list` | `agent-doc-administration` | none | Open-only agent list for SaaS admins; no doc edit is started. |
| `route-audit-trace-dashboard-open-v1` | Audit/Trace | `open audit trace`, `show audit dashboard` | `surface-audit-trace-dashboard` | `audit.trace.dashboard.read` | none | Open-only investigation dashboard; no export, note append, or evidence mutation. |
| `route-audit-trace-search-open-v1` | Audit/Trace | `search traces`, `find provider failures` | `surface-audit-trace-search` | `audit.trace.search` | none | Open-only scoped search surface; no unredacted export or hidden trace enumeration. |
| `route-governance-policy-dashboard-open-v1` | Governance/Policy | `open governance`, `show policy dashboard` | `surface-governance-policy-dashboard` | `governance.policy.read` | none | Open-only policy settings dashboard; no default/override write is submitted. |
| `route-governance-policy-inventory-open-v1` | Governance/Policy | `show policy inventory`, `show policy settings`, `show overridden policies` | `surface-governance-policy-inventory` | `governance.policy.read` | none | Open-only all-policy inventory; no default/override write is submitted. |

Future deterministic routes should be generated only from catalog rows whose prompt examples are high-confidence, whose required capability hint is already enforced by the target surface/action path, and whose prefill fields are scalar/browser-safe. Ambiguous, compound, destructive, approval-gated, provider-dependent, or missing-target prompts must fall back to model-backed chat or open a confirmation/recovery surface without submitting a command.


## Shared `human_chat_tool_plan` adapter path

`human_chat_tool_plan` is a separate governed adapter path for execution-oriented chat requests that are not safely handled by deterministic surface routing. It does not replace this catalog's router-first rule: high-confidence open/prefill prompts still route to a no-mutation surface before model-backed planning is considered. Compound, consequential, provider-dependent, or multi-step prompts may become chat tool-plan candidates only when every intended step is represented in the selected workstream's backend-owned chat tool catalog.

The shared adapter contract is:

1. **Plan proposal only:** the workstream agent may propose a catalog-bound plan surface using governed model/runtime context, visible surface/action metadata, and read/evidence tools. Proposal generation is no-mutation and cannot authorize work.
2. **Explicit human confirmation:** execution is forbidden until the signed-in human confirms the exact `planId`, `planSnapshotId`, selected `AuthContext`, requested/confirmed actor, step hashes, required capabilities, side effects, approval requirements, and idempotency root.
3. **Backend authorization:** every confirmed step is reauthorized server-side against the selected `AuthContext`, human authority, workstream catalog, governed tool boundary, lifecycle state, approval policy, and tenant/customer ownership. Rail visibility, prompt text, agent text, and frontend state never grant authority.
4. **Transaction/idempotency:** each step is its own transaction boundary with its own action id, browser tool id where applicable, governed tool id, capability id, input schema ref, idempotency key, correlation id, trace refs, result surface, and dependency/output binding. Successful prior steps remain committed if a later dependent step fails unless a cataloged compensating action exists.
5. **Safe denial and validation:** stale, expired, modified, cross-context, cross-tenant/customer, out-of-catalog, unauthorized, missing-provider/runtime/tool-boundary, invalid-input, approval-required, or confirmation-mismatch requests return a typed chat tool-plan system-message surface with `noDirectMutation=true`, `noFakeSuccess=true` when provider/model work is blocked, redaction, and trace refs.
6. **Trace obligations:** proposal, confirmation, denial, step started/completed/failed/skipped, provider-blocked, idempotent replay, partial failure, and recovery outcomes emit durable work/audit trace facts using event types prefixed by `human_chat_tool_plan.*`.

Shared plan surfaces available to every foundation workstream:

| Surface contract | Surface type | Purpose | Required behavior |
|---|---|---|---|
| `chat_tool_plan.proposal.v1` | `chat_tool_plan_proposal` / decision card | Show proposed steps, inputs, side effects, capabilities, approval gates, idempotency, no-mutation notice, and trace refs. | `status=waiting-for-human`; no tool execution. |
| `chat_tool_plan.confirmation.v1` | `chat_tool_plan_confirmation` | Plan-bound confirmation affordance. | Requires exact snapshot acknowledgement; cannot edit-and-submit a changed plan. |
| `chat_tool_plan.result.v1` | `chat_tool_plan_result` / workflow status | Show completed, failed, skipped, recovery, result surfaces, idempotency replay, and trace refs. | Partial failure must not hide committed steps or duplicate successful idempotent work. |
| `chat_tool_plan.system_message.v1` | `system-message` | Safe denial, unavailable, stale, expired, out-of-catalog, provider/runtime blocked, or capability-denied result. | No hidden target enumeration, no fake success, no authority expansion. |

First-pass representative `human_chat_tool_plan` catalog entries reuse the same governed tool ids as their corresponding surface actions:

| Workstream | Representative prompt | Shared action ids | Shared governed tool ids | Capability ids | Expected result surfaces |
|---|---|---|---|---|---|
| My Account | `change my theme to Obsidian Dark` | `action-update-my-settings` | `my_account.update_profile_settings` | `my_account.update_profile_settings` | `surface-my-settings` |
| User Admin | `create org "Org 1", and invite mckee.hugh@gmail.com as an org admin` | `action-submit-organization-create`; `action-submit-organization-admin-invitation` | `manage-organizations`; `manage-organization-admins` | `saas_owner.tenant.manage`; `saas_owner.organization_admin.invite` | `surface-user-admin-organization-detail`; `surface-user-admin-invitation-detail` |
| Agent Admin | `edit the support agent prompt to be friendlier` | `action-agent-doc-edit-start` | `draft-agent-doc-edit` | `agent-doc-administration` | `surface-agent-admin-edit-session` |
| Audit/Trace | `append investigation note "provider blocked; retry after config" to this trace` | `action-audit-trace-append-investigation-note` | `draft-investigation-note` | `audit.trace.investigation_note.append` | `surface-audit-trace-investigation-note` |
| Governance/Policy | `allow SalesAgent to send emails immediately` | `action-governance-policy-set-override` | `governance.policy.set_override` | `governance.policy.override` | `surface-governance-policy-edit` |

High-impact actions such as hard-platform-security override attempts, account disabling, role grants, trace export delivery, and support-access grants remain blocked, approval-gated, or surface-only until their exact confirmation, prerequisite, authorization, and recovery semantics are modeled in a later bounded task. Agent Admin no longer has a separate managed-agent activation/rollback lifecycle; saving an agent doc creates the new current version immediately.

## Expanded `human_chat_tool_plan` catalog classification intent

The expanded catalog is current intent for later implementation tasks; it does not claim runtime expansion until backend/frontend/tests are completed. Every workstream must continue to evaluate deterministic surface routing before chat tool planning. These classes are mutually exclusive for a specific adapter exposure:

- `chat-executable-now`: current-intent candidate for exact-confirmed chat execution using an existing backend-authorized surface action, selected `AuthContext`, idempotency, trace, and result surface.
- `chat-proposal-only`: current-intent candidate for creating/reading an inert proposal, draft, advisory task, or simulation result; it never commits final authority or lifecycle state.
- `approval-gated`: may be proposed or routed only when a separate approval/decision/confirmation surface remains authoritative; chat confirmation alone is insufficient.
- `surface-only`: must remain a structured browser surface action because target context, evidence review, field validation, or recovery UX is authoritative in the surface.
- `router-only`: deterministic no-mutation open/prefill route, not a chat execution step.
- `internal-only`: backend/service/provider/support path not appropriate for direct chat catalog exposure.
- `blocked-pending-design`: missing prerequisite design/runtime policy/test coverage for safe chat exposure.
- `out-of-scope`: outside this five-workstream foundation catalog expansion.

Expanded coverage by workstream:

| Workstream | Accepted expanded catalog classes | Blocked/surface-only rationale |
|---|---|---|
| My Account | `chat-executable-now` for profile/settings updates and in-app notification lifecycle/preferences. | Context switch, sign-out, digest disposition, external notification provider controls, and provider readiness remain `surface-only`, `approval-gated`, or `blocked-pending-design` because they need backend-authored choices, browser/session UX, provider fail-closed proof, or external-channel policy. |
| User Admin | `chat-executable-now` for role-pinned invitations, visible invitation resend/revoke, Organization/Customer create, and visible Organization/Customer rename. `chat-proposal-only` for role/capability preview and advisory review evidence. | Destructive lifecycle, role/status/account changes, support access, identity recovery, and access-review disposition remain `approval-gated`, `surface-only`, or `blocked-pending-design` because authority, last-admin/self-action, provider/outbox, and recovery semantics need dedicated surfaces/policies. |
| Agent Admin | `chat-proposal-only` for AI-assisted doc-edit proposals and `chat-executable-now` only for backend-authorized open/list/read actions and exact Save/Cancel/Restore/Delete confirmations already represented by Agent Admin surfaces. | Whole-agent create/delete, model settings, tool permission administration, separate activation/rollback lifecycles, tenant/org governance, and unsupported target discovery remain out of scope. |
| Audit/Trace | `chat-executable-now` for scoped redacted search/detail/timeline/failure/guide reads and idempotent investigation-note append. | Export, model-backed summaries, raw evidence, hidden trace delivery, and ingestion/projection internals remain `approval-gated`, `surface-only`, `internal-only`, or `blocked-pending-design` to preserve redaction, provider fail-closed behavior, and no hidden-target enumeration. |
| Governance/Policy | `chat-executable-now` for scoped list/effective-policy/history reads; `chat-proposal-only` for preparing simple default/override/reset writes that still require exact confirmation, required reason, backend authorization, idempotency, and trace. | Complex policy scripts, simulations, legal compliance workflows, policy-edit approval workflows, notifications by default, enterprise delegation, and hard-platform-security overrides remain out of scope or denied. |

## Catalog: My Account

catalog-workstream: `my-account-agent`

| Surface id/title | Purpose | Prompt examples | Required capabilities | Prefill fields | Forbidden direct effects |
|---|---|---|---|---|---|
| `surface-my-account-dashboard` — My Account Dashboard | Personal command center for selected-context attention, authority, profile/settings, notification, digest, and workstream-open recovery. | `open my account`, `show my account dashboard`, `what needs my attention?` | visible `my-account-agent`; `my_account.view_summary` for dashboard data | none | Do not switch context, update settings, dismiss notifications, start digest work, or open hidden workstreams. |
| `surface-my-profile` — My Profile | Self-service browser-safe profile facts and editable personal profile fields. | `open my profile`, `change my display name`, `show my identity info` | `my_account.view_summary`; `my_account.update_profile_settings` only on submit | `displayName` may be drafted only after a later explicit route | Do not edit roles, membership status, provider identity, passwords, or other accounts. |
| `surface-my-settings` — My Settings | Personal theme, locale/timezone, and in-app preference entry point. | `open settings`, `change my theme`, `show notification preferences` | `my_account.view_summary`; `my_account.update_profile_settings` only on submit | `preferredThemeId`, `locale`, `timeZone` deferred until option validation exists | Do not change tenant branding, authorization, hidden notification categories, external delivery, or provider/model config. |
| `surface-my-context` — Context and Authority | Shows selected `AuthContext`, available authorized contexts, and context-switch effects. | `show my context`, `switch context`, `why can't I see this?` | `my_account.view_context`; `core.access.context.select` only on explicit submit | `selectedContextId` deferred to backend-authored choices | Do not grant roles/capabilities, enumerate hidden tenants/customers, or switch to a non-authorized context. |
| `surface-my-account-notification-center` — Notification Center | Personal in-app notification triage and preference summary. | `open notifications`, `show unread notifications`, `notification center` | `notification.list_my_account_center`; lifecycle capabilities only on submit | `category`/`status` filter deferred | Do not resolve source tasks, complete attention items, expose external-channel/provider state, or mutate hidden notifications. |
| `surface-my-account-personal-attention-digest-progress` — Personal Digest Progress | Tracks durable personal attention digest/export worker progress or blocker. | `start personal digest`, `show digest progress`, `where is my attention summary?` | `my_account.personal_attention_digest.start/read/cancel` as appropriate | `timeWindow` deferred until task route validation exists | Do not fabricate model-less summaries, mutate source attention, or accept/reject results. |
| `surface-my-account-personal-attention-digest-result` — Personal Digest Result | Review advisory digest/export output and evidence disposition. | `show my digest result`, `review attention summary` | `my_account.personal_attention_digest.read/accept_result/reject_result` | none | Do not complete source work, create fake provider output, or change source attention. |
| `surface-my-account-personal-attention-digest-blocked` / `surface-my-account-open-denied` — Recovery messages | Fail-closed provider/runtime or unavailable target recovery. | `why was that blocked?`, `open denied details` | safe recovery read for selected visible context | none | Do not reveal hidden targets, hidden workstreams, provider secrets, or grant missing authority. |

Deferred or ambiguous My Account routing: context switching, notification lifecycle changes, digest start/cancel, and profile/settings saves require explicit backend-authored options and submit actions. Free-form prompts may open the relevant surface but must not perform those actions.

## Catalog: User Admin

catalog-workstream: `user-admin-agent`

| Surface id/title | Purpose | Prompt examples | Required capabilities | Prefill fields | Forbidden direct effects |
|---|---|---|---|---|---|
| `surface-user-admin-dashboard` — User Admin Dashboard | Attention-first access administration command center and branch router. | `open user admin`, `show access admin dashboard`, `what user admin work needs attention?` | `user_admin.view_overview` | none | Do not create users, change roles/status, send invitations, or reveal hidden populations. |
| `surface-user-admin-users` — User Directory | Scoped user/member/invitation discovery; rows open inspection/task surfaces. | `show users`, `find user alex@example.com`, `user directory` | `user_admin.list_members` or selected-scope equivalent | `query` safe text filter deferred | Do not change memberships, roles, invitations, or support access inline. |
| `surface-user-admin-user-detail` — User Detail | Lifecycle-aware inspection for one visible account/membership/invitation context. | `open user alex@example.com`, `show this user's access` | user read/list capability for selected scope | `recordId`/`email` only from backend-visible rows | Do not mutate roles/status/support access inline or enumerate hidden users. |
| `surface-user-admin-invitation-create` — Invite User | Single-purpose scoped invitation form. | `invite user alex@example.com`, `invite Alex to this tenant` | `user_admin.invite_user` | `email`, later `displayName`, `roles` only from backend options | Do not send an invitation until user submits; do not choose unauthorized roles or expose tokens/provider payloads. |
| `surface-user-admin-invitation-detail` — Invitation Detail | Invitation lifecycle inspection and delivery state. | `show invitation`, `open pending invitation` | `user_admin.acceptance_status.read` | invitation id only from visible rows | Do not resend/revoke inline, expose tokens, or reveal hidden invitations. |
| `surface-user-admin-invitation-resend-confirmation` / `surface-user-admin-invitation-revoke-confirmation` | Dedicated lifecycle confirmation surfaces for invitation resend/revoke. | `resend this invitation`, `revoke invitation` | `user_admin.resend_invitation` / `user_admin.revoke_invitation` | visible invitation id, reason | Do not send/revoke without confirmation, idempotency, provider/outbox checks, and backend authorization. |
| `surface-user-admin-membership-status-confirmation` | Disable/suspend/reactivate/remove membership/account confirmation. | `disable this user`, `reactivate member` | `user_admin.update_member_status` | visible membership/account id, target status, reason | Do not bypass last-admin/self-action protections or mutate hidden memberships. |
| `surface-user-admin-role-change-preview` | Role/capability delta decision card before role mutation. | `change role to tenant admin`, `preview role change` | `user_admin.preview_role_change` / `user_admin.change_member_roles` | visible membership id, proposed roles from backend options | Do not directly grant roles, expand capability, or skip approval/last-admin checks. |
| `surface-user-admin-support-access-grant` / `surface-user-admin-support-access-revoke-confirmation` | Support-access grant/extend or revoke task surfaces. | `grant support access`, `revoke support access` | `user_admin.support_access.grant_revoke_extend` | visible subject id, expiry option, reason | Do not expose support internals, grant access without policy approval, or bypass expiry limits. |
| `surface-user-admin-access-review-task` | Durable access-review progress/result and human disposition. | `run access review`, `show access review result` | `user_admin.access_review.*` | review target/filter deferred | Do not let autonomous output directly change roles, status, invitations, or support access. |
| `surface-user-admin-identity-exception-review` | Identity link/relink exception review and approved recovery routing. | `review identity exception`, `fix account link` | `user_admin.identity_relink.*` | visible identity exception id deferred | Do not expose raw provider ids/payloads or complete recovery without approved backend flow. |
| `surface-user-admin-saas-owner-admins` / `surface-user-admin-saas-owner-admin-invitation-create` | SaaS Owner admin directory and app-owner admin invite form. | `show app owner admins`, `invite saas owner admin alex@example.com` | `saas_owner.admin.list` / `saas_owner.admin.invite` | create form may prefill `email`, `displayName`, `reason`; roles backend-limited | Do not expose tenant/customer app data, hidden app-owner admins, or send invitations without submit. |
| `surface-user-admin-organization-directory` — Organization Directory | SaaS Owner Organization discovery; rows open Organization detail. | `show organizations`, `organization directory`, `list organizations` | `saas_owner.tenant.read` / `saas_owner.organization.list` | `query` deferred | Do not create, rename, suspend, archive, reactivate, or expose hidden Organizations inline. |
| `surface-user-admin-organization-detail` — Organization Detail | Lifecycle-aware Organization inspection and task router. | `open organization Org 1`, `show organization details` | `saas_owner.organization.read` | `organizationId` only from visible rows | Do not mutate lifecycle/name/admins inline or expose tenant app data. |
| `surface-user-admin-organization-create` — Create Organization | Single-purpose Organization/Tenant boundary create form. | `create organization "Org 1"`, `add organization "Org 1"` | `saas_owner.tenant.manage` / `saas_owner.organization.manage` | `organizationName`, optional `reason` later | Do not create until form submit; do not create duplicate hidden Organizations or accept raw tenant/provider ids. |
| `surface-user-admin-organization-rename` / `surface-user-admin-organization-suspend-confirmation` / `surface-user-admin-organization-reactivate-confirmation` | Dedicated Organization edit and lifecycle confirmation surfaces. | `rename this organization`, `suspend organization`, `reactivate organization` | `saas_owner.organization.rename/suspend/reactivate/archive` | visible `organizationId`, `organizationName`, `reason` | Do not perform destructive/lifecycle changes from prompt or without confirmation/idempotency. |
| `surface-user-admin-organization-admins` / `surface-user-admin-organization-admin-invitation-create` / `surface-user-admin-organization-admin-detail` | Organization Admin directory, bootstrap invite, and detail surfaces for one visible Organization. | `show organization admins`, `invite org admin alex@example.com` | `saas_owner.organization_admin.list/invite/manage` | visible `organizationId`; invite `email`, `displayName`, `reason` | Do not expose tenant app data, hidden admins, or invite without submit. |
| `surface-user-admin-customer-directory` / `surface-user-admin-customer-detail` | Tenant Customer discovery and lifecycle-aware inspection. | `show customers`, `open customer Acme` | `tenant.customer.list/read` | `query` or visible `customerId` deferred | Do not create/rename/suspend/reactivate customers inline or cross customer scope. |
| `surface-user-admin-customer-create` / `surface-user-admin-customer-rename` / `surface-user-admin-customer-suspend-confirmation` / `surface-user-admin-customer-reactivate-confirmation` | Customer create/edit/lifecycle task surfaces. | `create customer "Acme"`, `rename this customer`, `suspend customer` | `tenant.customer.create/rename/suspend/reactivate/archive` | `customerName`, visible `customerId`, `reason` after route validation | Do not mutate customer lifecycle without dedicated form/confirmation and selected Organization authority. |
| `surface-user-admin-customer-admins` / `surface-user-admin-customer-admin-invitation-create` / `surface-user-admin-customer-admin-detail` | Customer Admin directory, invite, and detail for one visible Customer. | `show customer admins`, `invite customer admin alex@example.com` | `tenant.customer_admin.list/invite/manage` | visible `customerId`; invite `email`, `displayName`, `reason` | Do not expose sibling-customer data, hidden admins, or invite without submit. |
| `surface-user-admin-system-message` | Safe denial, validation, provider/outbox/model blocked, stale, conflict, and no-op recovery. | `why was access admin blocked?`, direct denied target recovery | safe selected-context User Admin visibility | none | Do not enumerate hidden targets, leak provider/model/outbox internals, or convert denial into authority. |

Deferred or ambiguous User Admin routing: destructive prompts, role/status changes, support-access changes, identity recovery, access-review accept/reject, and target-specific admin/customer/Organization actions require visible target context and dedicated task/confirmation surfaces. Compound prompts with `and`, `then`, or multiple targets are intentionally ambiguous and should fall back.

## Catalog: Agent Admin

catalog-workstream: `agent-admin-agent`

| Surface id/title | Purpose | Prompt examples | Required capabilities | Prefill fields | Forbidden direct effects |
|---|---|---|---|---|---|
| `surface-agent-admin-blank` — Blank Agent Admin workstream | Empty persisted workstream state with Show dashboard, Show agents, Clear workstream, and composer. | `clear agent admin`, first open with no prior surface | SaaS admin visibility / `agent-doc-administration` | none | Do not open hidden docs, start edits, or create changes. |
| `surface-agent-admin-dashboard` — Agent Admin Dashboard | Optional quick access dashboard with total agent count and top five recently changed agents. | `show agent admin dashboard`, `open agent admin dashboard` | `agent-doc-administration` | none | Do not start an edit or imply needs-attention queues. |
| `surface-agent-admin-agent-list` — Agent List | Filter all agents by agent name and workstream/domain; rows open agent detail. | `show agents`, `list agents`, `find support agent` | `agent-doc-administration` | optional safe search text | Do not create/delete agents or edit docs inline. |
| `surface-agent-admin-agent-detail` — Agent Detail | Show editable agent name/purpose, prompt link, skills, reference docs, and trace entry points. | `open support agent`, `show billing agent docs` | `agent-doc-administration` | visible agent id/name | Do not create/delete whole agents. |
| `surface-agent-admin-prompt-doc` — Prompt Doc | View current or historical prompt Markdown, version metadata, edit request, history, diff, restore, and current-version edit input. | `edit this agent prompt`, `show prompt version 3` | `agent-doc-administration` | visible agent/doc context | Do not edit historical versions directly. |
| `surface-agent-admin-skill-doc` — Skill Doc | View/edit one skill with purpose/description, Markdown content, reference docs, version history, diff, restore, and current-version edit input. | `edit onboarding skill`, `show skill version 2` | `agent-doc-administration` | visible skill context | Do not edit historical versions directly. |
| `surface-agent-admin-skill-reference-doc` — Skill Reference Doc | View/edit one skill reference doc with name, short description, Markdown content, version history, diff, restore, and current-version edit input. | `edit this skill reference doc`, `show reference doc version 4` | `agent-doc-administration` | visible reference doc context | Do not edit historical versions directly. |
| `surface-agent-admin-edit-session` — AI-assisted Edit Session | Iterative editing-agent proposal with full proposed doc, summary, advisory warnings/risks, Show diff, refinement input, Save, and Cancel. | `make this prompt friendlier`, `improve this skill` | `agent-doc-administration` | free-form edit request when target is clear | Do not save without explicit Save action; do not edit older base versions. |
| `surface-agent-admin-version-history` / `surface-agent-admin-version-diff` | Browse integer versions and show selected version `N` diffed only against `N-1`. | `show version history`, `show diff for version 7` | `agent-doc-administration` | visible doc/version | Do not compare arbitrary non-adjacent versions or enable edit input on history. |
| `surface-agent-admin-create-skill` / `surface-agent-admin-delete-skill-confirmation` | Create skills with name, purpose, editing-agent-drafted content; permanently delete skills and reference docs after confirmation. | `create a skill`, `delete this skill` | `agent-doc-administration` | skill name/purpose/request where safe | Do not create/delete whole agents; do not allow restore after skill deletion. |
| `surface-agent-admin-create-reference-doc` / `surface-agent-admin-delete-reference-doc-confirmation` | Create reference docs under a skill with name, short description, editing-agent-drafted content; permanently delete after confirmation. | `add reference doc`, `delete this reference doc` | `agent-doc-administration` | reference name/description/request where safe | Do not create reference docs outside a skill. |
| `surface-agent-admin-runtime-traces` | Show runtime `readSkill` and `readReferenceDoc` metadata filtered by agent, doc, and time range. | `show skill read traces`, `show reference doc reads` | `agent-doc-administration` | filter hints | Do not show full read content in trace rows. |
| `surface-agent-admin-system-message` | Denial, clarification, unsupported request, provider unavailable, stale version, deleted doc, or safer alternative. | `why was that blocked?` | safe Agent Admin visibility | none | Do not grant missing SaaS admin authority. |

Deferred or ambiguous Agent Admin routing: if the target agent/doc/action is unclear, the workstream agent should ask a clarifying question or open the safest list/detail surface. Model settings, tool permission administration, tenant/org governance, whole-agent creation/deletion, and separate activation/rollback flows are out of scope.

## Catalog: Audit/Trace

catalog-workstream: `audit-trace-agent`

| Surface id/title | Purpose | Prompt examples | Required capabilities | Prefill fields | Forbidden direct effects |
|---|---|---|---|---|---|
| `surface-audit-trace-dashboard` — Audit/Trace Dashboard | Investigation command center with scoped counters, failure/denial attention, and evidence entry points. | `open audit trace`, `show audit dashboard`, `what failed?` | `audit.trace.dashboard.read` / `audit.trace.read` | none | Do not reveal hidden counts, export evidence, append notes, or expand investigation authority. |
| `surface-audit-trace-search` — Trace Search | Scoped search across redacted audit/work/agent/provider/policy evidence. | `search traces for denial`, `find provider failures`, `show traces for correlation abc` | `audit.trace.search` | `query`, `timeWindow`, `category`, `correlationLabel` | Do not search local caches, expose raw event bodies, or enumerate hidden trace ids. |
| `surface-audit-trace-detail` — Trace Detail | Read-only browser-safe evidence detail for one authorized trace/event. | `open trace detail`, `show event evidence` | `audit.trace.detail.read` | visible `traceId`/detail key only from authorized rows/links | Do not edit evidence, expose raw payloads/prompts/secrets, or confirm hidden trace existence. |
| `surface-audit-trace-timeline` — Correlation Timeline | Ordered correlation timeline across authorized redacted evidence categories. | `open timeline`, `show correlation timeline`, `what happened in order?` | `audit.trace.timeline.read` | `correlationId`, `timeWindow` only when authorized | Do not construct timelines client-side or reveal omitted hidden categories. |
| `surface-audit-trace-failure-evidence` | Denial/provider/tool/model/runtime failure evidence and recovery. | `show failure evidence`, `why was provider blocked?` | `audit.trace.failureEvidence.read` | visible failure/category/correlation refs | Do not expose provider secrets/raw errors or mutate source failures. |
| `surface-audit-trace-investigation-guide` | Advisory investigation guidance with next safe surfaces. | `guide this investigation`, `what should I check next?` | `audit.trace.investigationGuide.read` | visible trace/filter context | Do not expand authority, make policy decisions, or alter source evidence. |
| `surface-audit-trace-export-request` | Policy-gated redacted export request. | `request redacted export`, `export these traces` | `audit.trace.export.request` | `reason`, `format`, visible scope/filter refs | Do not create unredacted browser export, skip approval, or include hidden evidence. |
| `surface-audit-trace-investigation-note` | Result/recovery surface for trace note append. | `add investigation note`, `record note on trace` | `audit.trace.investigation_note.append` | `noteText`, visible trace/correlation ref after confirmation | Do not mutate source trace/policy/authorization or append notes without idempotent backend action. |
| `surface-audit-trace-summary-progress` / `surface-audit-trace-summary-review` | Model-backed redacted advisory summary task progress and human review. | `summarize this investigation`, `show audit summary result` | `audit.trace.summary_task.*` | visible trace/filter refs, summary reason deferred | Do not fabricate model-less summaries, accept/reject without human review, or mutate retained evidence. |

Deferred or ambiguous Audit/Trace routing: note append, export, and summary starts are consequential or provider-dependent and should open request/progress/review surfaces only; exact trace/correlation routing requires a backend-visible trace reference. Ambiguous raw ids or cross-scope references must deny/fall back without enumeration.

## Catalog: Governance/Policy

catalog-workstream: `governance-policy-agent`

| Surface id/title | Purpose | Prompt examples | Required capabilities | Prefill fields | Forbidden direct effects |
|---|---|---|---|---|---|
| `surface-governance-policy-dashboard` — Governance/Policy Dashboard | Simple policy settings overview with overridden counts, recent changes, and safe shortcuts. | `open governance`, `show policy dashboard`, `show policy settings` | `governance.policy.read` | none | Do not write defaults or overrides from dashboard text alone. |
| `surface-governance-policy-inventory` — Policy Inventory | List/search all visible policies with default, override, effective value, value type, scope, and overridden indicator. | `show policy inventory`, `show policy settings`, `show overridden policies` | `governance.policy.read` | `query`, workstream/agent/tool/role filters deferred | Do not edit policies inline or enumerate hidden policy scopes. |
| `surface-governance-policy-effective-detail` — Effective Policy Detail | Explain one policy's SaaS default, tenant override, winning scope, effective value, and runtime decision semantics. | `show effective policy`, `why can SalesAgent send email?`, `show this policy` | `governance.policy.read` | visible policy/scope ref only from authorized rows | Do not mutate policy or expose hidden scope internals inline. |
| `surface-governance-policy-edit` — Policy Edit | Set SaaS default, set tenant override, or reset override for simple boolean/counter policies with required reason. | `allow SalesAgent to send emails immediately`, `set max retries to 3`, `reset this policy to default` | `governance.policy.default.manage` or `governance.policy.override` | policy id/scope/value/reason only after backend route validation | Do not submit without explicit action, required reason, idempotency, backend authorization, and hard-platform-security checks. |
| `surface-governance-policy-history` — Policy History | Show direct policy changes and practical runtime outcome links where available. | `show policy history`, `who changed this policy?`, `show outcomes for this policy` | `governance.policy.read` | policy/scope filters from visible context | Do not expose hidden tenant/customer facts, raw prompts, raw provider payloads, secrets, or raw tool payloads. |
| `surface-governance-policy-system-message` | Safe governance denial, validation, stale/conflict, hard-platform-security, or recovery state. | `why was policy action denied?`, `why can't I override this?` | safe selected-context Governance/Policy visibility | none | Do not reveal hidden policies, hidden scopes, raw provider/model details, or convert denial into authority. |

Deferred or ambiguous Governance/Policy routing: default/override/reset writes require visible target policy/scope, simple supported value type, required reason, explicit submit/confirmation, idempotency, backend authorization, and hard-platform-security checks. Deterministic routing may open or prefill relevant surfaces only when the target and mode are unambiguous and must never submit the command.

## Catalog safety checks

- All catalog text is descriptive. Capabilities are hints for backend authorization, not grants.
- Router prefill is limited to scalar browser-safe draft fields that remain editable and review-required.
- Create/edit/lifecycle/approval/export/note/provider-backed asks open structured surfaces or fall back; they never execute through the router.
- Hidden, cross-tenant/customer, stale, or unsupported targets must result in safe denial/recovery or model fallback without enumeration.
- Catalog rows intentionally include deferred notes for ambiguous/destructive/provider-dependent surfaces so later routing tasks can choose narrow high-confidence patterns only.
