# Behavior: Governance/Policy

## Current-state behavior

Governance/Policy owns the policy lifecycle for foundation AI-first SaaS controls. It starts from a role-scoped dashboard, policy catalog, policy detail, draft/edit, simulation, decision-card, exception, rollback, and history surfaces. It records policy versions and decision evidence so downstream workstreams can enforce and explain policy-bound behavior.

## Agent behavior

`governance-policy-agent` may explain active policy, summarize draft/active/rolled-back versions, help find impacted tools/workstreams, draft policy proposals, prepare simulation inputs, summarize simulation findings, assemble decision-card evidence, draft exception requests, and prepare rollback plans.

It cannot grant authority through prompt text, bypass backend authorization, approve decisions, activate policies, roll back versions, grant exceptions, expose secrets, override hard platform controls, or invent policy types outside the catalog. Model-backed turns use governed runtime configuration or fail closed.

## Mandatory policy categories

Foundation policy categories are:

- agent/tool authority and ToolPermissionBoundary policy;
- approval gates, risk/confidence/impact thresholds, and human-review requirements;
- exception policies with scoped reason, owner, expiry, evidence, and review state;
- runtime enforcement policies with simple boolean/counter/limit values where appropriate;
- model, prompt, skill, reference, rubric, and governed-document activation policy when behavior-shaping authority changes;
- trace visibility, retention, redaction, and downstream enforcement evidence policy, subordinate to hard platform controls.

Domain-specific business policies are extension-owned until a business domain adds them. Complex scripting, arbitrary rule expressions, legal workflow engines, and autonomous authority-expanding commits are outside current foundation scope.

## Draft behavior

Draft creation produces a versioned `PolicyProposal` with policy id/category, proposed clauses or simple values, target scope, affected agents/workstreams/tools/roles/customers/accounts, rationale, risk/impact/confidence, expected behavior changes, requester, idempotency key, and trace reference. Drafts do not affect runtime until approved and activated.

Draft surfaces and chat-plan proposals must make side effects explicit and require a reason. Duplicate draft requests with the same idempotency key return the existing draft/result without duplicate traces.

## Simulation behavior

Simulation evaluates a draft or rollback candidate against selected current-policy state, representative traces, affected tool/workstream mappings, known exceptions, and approval gates. It returns expected allow/deny/governed outcomes, changed decisions, risk/impact/confidence, evidence gaps, partial-failure markers, and whether human approval is required before activation.

Simulation is evidence, not activation. Missing evidence or unavailable replay data yields partial-failure results and review blockers rather than silent success.

## Decision behavior

Human reviewers decide from decision cards. Available actions are approve, reject, request evidence, modify/counterpropose, defer, escalate, mark exception-required, activate approved version when allowed, or start rollback review. Each decision stores reviewer, selected `AuthContext`, role/capability basis, evidence considered, policy clauses, simulation refs, rationale, alternatives, uncertainty, risk/impact/confidence, deadline/SLA when present, and trace links.

Approval is required before activating policy changes that expand authority, alter approval gates, grant exceptions, affect governed agent/tool permissions, change trace visibility/retention, or roll back active policy versions. Rejections and requests for evidence do not change active runtime policy.

## Activation behavior

Activation converts an approved proposal into the active policy version for its authorized scope. Activation validates approval state, freshness, catalog membership, selected scope, separation-of-duty requirements, idempotency key, and publication target. It records old version, new version, activation time, actor, approval ref, simulation ref, affected workstreams/tools/roles, and runtime publication status.

Activation is a policy-version transaction boundary. If publication to one scope or downstream projection fails, the result surface distinguishes committed, not-committed, and partial-publication states and links trace evidence. Unapproved, stale, cross-context, or hidden-target activations are denied.

## Exception behavior

Exception review grants, denies, revokes, or expires scoped deviations from active policy only where the policy allows exceptions. Each exception has owner, reason, affected policy/version/scope, allowed action, expiry, evidence, reviewer, and trace links. Expired exceptions must not authorize runtime behavior. Exception denial returns a safe result/system-message surface and trace.

## Rollback behavior

Rollback restores a prior approved version or revokes a problematic exception through a separate rollback decision card. Rollback requires reviewer authority, reason, impact summary, prior-version target, idempotency key, and runtime publication checks. It creates a new rollback commit record instead of deleting history.

Rollback is a single-version transaction boundary with result and partial-failure surfaces. Repeated rollback with the same idempotency key returns the original result without duplicate commits or traces.

## Runtime enforcement behavior

Runtime policy checks return an effective decision with human-readable explanation, active version, matching policy clauses, exception status, approval requirement if any, and trace reference. Downstream workstreams must cite governance-policy enforcement traces when policy affects action availability, agent/tool execution, approval routing, denials, exception use, or rollback recovery.

## Edge cases

Unsupported policy ids/categories/scopes/value types, missing reasons, missing simulation evidence, stale versions, conflicting active versions, expired exceptions, inactive users/memberships, missing selected context, unauthorized cross-tenant/customer attempts, duplicate idempotency keys, and attempts to override hard platform controls return safe validation/denial/result states and emit traces.

Unsupported business-domain requests are routed to domain extension guidance rather than silently adding complex policy machinery.
