# Tests: Governance/Policy

## Acceptance

- Given an authorized caller with selected `AuthContext`, when they open Governance/Policy, then the dashboard and catalog render only scoped data, pending decisions, simulation findings, exceptions, rollback candidates, effective policy summaries, and authorized actions.
- Given an authorized policy operator drafts a foundation policy proposal with a reason and idempotency key, then a versioned draft is recorded, active runtime policy does not change, and a draft trace/result surface is returned.
- Given a draft that changes authority, approval gates, exceptions, managed-agent behavior policy, trace visibility, or runtime enforcement, when simulation is run, then simulation findings identify expected allow/deny/governed outcomes, changed decisions, risk/impact/confidence, evidence gaps, and required approval gates without activating the policy.
- Given a reviewer with authority opens a decision card, then the card shows recommendation, evidence, simulation refs, policy clauses, risk, confidence, impact, alternatives, known gaps, available actions, deadline/SLA when present, and trace links.
- Given a reviewer approves a valid decision card, when activation is submitted with matching approved decision, reason, freshness token, and idempotency key, then the policy version activates as one transaction boundary, history and traces are recorded, downstream runtime policy evaluation uses the new version, and the result surface links evidence.
- Given a reviewer rejects or requests evidence, then the active policy version is unchanged, attention/workflow state updates, and decision traces record rationale and evidence refs.
- Given an authorized exception reviewer grants a scoped exception with owner, reason, evidence, expiry, and idempotency key, then runtime enforcement recognizes the exception only for that scope/time and exception traces are recorded.
- Given an authorized rollback decision restores a prior approved version, then a rollback commit is appended, active policy changes to the target prior version, history remains append-only, runtime enforcement uses the restored version, and rollback traces are recorded.
- Given a runtime action checks policy, then the policy-decision trace records active version, matching clause/value, winning scope, exception status, approval requirement, actor/action context, and downstream workstream reference.
- Given an auditor or support user with authorized access opens policy history, then direct changes, simulation, decisions, exceptions, rollback records, and practical runtime outcome links are visible without exposing hidden tenant/customer facts or raw sensitive payloads.

## UI and surface coverage

- Dashboard attention items cover pending policy approvals, simulation findings, exception reviews, rollback decisions, denials, and partial failures.
- Catalog/search supports filters for policy name, category, lifecycle state, workstream, agent, tool/action, role, scope, exception state, and approval state.
- Detail surfaces show active version, draft versions, effective decision, exceptions, rollback targets, decision evidence, and authorized actions.
- Draft surfaces require reason, validate policy category/scope/value/clauses, show affected workstreams/tools/roles, and do not mutate active policy.
- Simulation surfaces show changed outcomes, evidence gaps, partial failures, risk/impact/confidence, and required approval gates.
- Decision-card surfaces support approve, reject, request evidence, modify/counterpropose, defer, escalate, mark exception-required, activate approved version where allowed, and start rollback where allowed.
- Result and partial-failure surfaces distinguish committed, not-committed, idempotent-replay, partial-publication, approval-required, denied, stale/conflict, and failed states.
- Related Agent Admin, User Admin, Audit/Trace, and domain workstream pages may deep-link into Governance/Policy surfaces, but Governance/Policy remains the central policy lifecycle and history view.

## Security and negative

- Disabled users, inactive memberships, missing selected context, missing capability, missing reviewer authority, self-approval when separation-of-duty requires another reviewer, and cross-tenant/customer requests are denied without protected-data leakage.
- Tenant admins cannot activate SaaS-wide policy versions unless selected `AuthContext` grants that authority.
- SaaS owner admins cannot silently overwrite tenant-owned approved versions or exceptions outside authorized scope.
- The Governance/Policy agent may explain, search, summarize, draft, and simulate when allowed, but cannot autonomously approve, activate, roll back, grant exceptions, or expand authority.
- Unsupported policy ids, categories, scopes, value types, missing reasons, stale versions, malformed thresholds/counters, hidden customer/account targets, invalid exception expiry, missing simulation evidence, and unapproved activation return safe validation/denial/`system_message` outcomes and emit traces.
- Attempts to override hard platform controls are denied: tenant isolation, backend authorization, secret/JWT/provider-key protection, raw prompt/model/provider payload protection, redaction boundaries, frontend secret boundaries, audit trace integrity, required human-governance gates, or platform integrity checks.
- Browser payloads never expose provider secrets, raw prompts, hidden authority state, JWTs, raw tool payloads, raw correlation/idempotency internals, raw model/provider payloads, or cross-tenant evidence.

## Idempotency and transaction behavior

- Repeating any draft, approval request, decision, activation, rollback, or exception action with the same idempotency key returns the existing result and does not duplicate drafts, decisions, commits, history, traces, runtime outcome links, or attention items.
- Activation is a single policy-version transaction boundary; partial downstream publication produces an explicit partial-failure result and trace.
- Rollback is a single policy-version transaction boundary; repeated rollback returns the same result and never deletes prior history.
- Exception grant/revoke/expire is an exception-state transaction boundary; expired exceptions do not authorize runtime behavior.
- Stale version, modified plan snapshot, expired plan, cross-context confirmation, or missing approval state denies execution before mutation.

## Observability and runtime-validation

- Denials, validation errors, effective-policy decisions, policy drafts, simulations, approval requests, reviewer decisions, activations, rollback actions, exception grants/denials/revocations/expiries, partial failures, stale/conflict outcomes, and hard-platform-security override attempts are verifiable through future local Akka/API/UI runtime-validation or readiness evidence.
- Policy-decision traces emitted by downstream workstreams cite Governance/Policy policy version, clause/value, exception state, approval gate, actor adapter/source, selected `AuthContext`, and result surface/workstream item.
- Decision traces distinguish recommendation from final human decision and link simulation findings, evidence, reviewer, rationale, and outcome follow-up.
- Trace redaction tests prove browser-visible trace summaries omit raw secrets, raw prompt/model/provider/tool payloads, hidden target existence, and raw idempotency/correlation internals.

## `human_chat_tool_plan` coverage

- Given deterministic surface routing can safely open or prefill a surface for policy reads/searches/detail/simulation/decision/exception/history requests, when a high-confidence no-mutation prompt is submitted, then the router returns that surface first and `human_chat_tool_plan` is not used.
- Given a representative command prompt such as **draft a policy requiring approval before this agent sends email**, **simulate this draft**, **send this draft for approval**, **activate the approved policy**, **roll back to the prior version**, or **grant a 7 day exception for this customer** and an authorized selected `AuthContext`, when the chat request is classified as `human_chat_tool_plan`, then the response is a no-mutation plan proposal surface that lists the relevant action, governed tool, capability `governance-policy-lifecycle`, schema, required reason, approval state, idempotency, side effects, result/partial-failure surface, and trace refs.
- Given a proposed plan has not been explicitly confirmed, when the request completes, then no policy/default/override/version/exception state mutation has occurred.
- Given the human confirms the exact `planId` and `planSnapshotId`, when backend authorization, catalog, validation, selected scope, required reason, approval state, hard-platform-security, and idempotency checks pass, then each step executes as an independent governed transaction boundary and returns the declared result or recovery surface.
- Given a modified, stale, expired, cross-context, cross-tenant/customer, missing-confirmation, out-of-catalog, unsupported-field, hidden-target, missing-reason, missing-approval, hard-platform-security, or unauthorized plan is confirmed, then execution is denied with a safe `system_message`, `noDirectMutation=true`, no hidden-target enumeration, and trace refs.
