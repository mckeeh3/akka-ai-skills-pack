# Capability: Managed agent governance

## Purpose

Let authorized agent stewards administer platform-level and tenant/organization-scoped managed agent behavior records, governed prompts, skills, references, manifests, tool permission boundaries, model policy references, seed imports, proposals, activation, rollback, and runtime trace review.

## Actors and scope

- SaaS Owner/App Admin: may open Agent Admin and create, review, approve, activate, or roll back platform-level managed-agent defaults, seed material, and app-owner managed agents through a SaaS Owner selected `AuthContext` with explicit platform `agent_admin.*` capabilities. This does not grant tenant/customer application-data access or tenant-specific behavior authority by implication.
- Tenant/Organization Admin: may open Agent Admin and create, review, approve, activate, or roll back tenant/organization-scoped managed-agent behavior changes through a selected `TENANT_ADMIN` / `tenant-admin` AuthContext with explicit tenant-scoped `agent_admin.*` capabilities.
- Customer Admins and customer-scoped users: not authorized for Agent Admin reads or behavior changes, because managed-agent behavior records are tenant/organization-scoped rather than customer-scoped.
- Auditor: may read audit/trace evidence through Audit/Trace capabilities, but audit access alone does not allow Agent Admin catalog, prompt, skill, reference, manifest, model, tool-boundary, seed, or prompt-risk task reads.
- Agent Admin functional agent: explains, drafts proposals, identifies risk, and guides safe activation only within the actor's authorized platform or tenant/organization governance scope; it cannot grant itself tools, data scope, model access, or approval authority.

## Governed tools and exposure

- `list-agent-catalog` (`browser-tool`, `agent-tool` read): scoped managed agents and status.
- `read-agent-behavior-detail` (`browser-tool`, `agent-tool` read): current versions, manifests, boundaries, model refs, and traces with redaction.
- `draft-agent-behavior-proposal` (`browser-tool`, `agent-tool` proposal): creates draft/proposed changes.
- `submit-agent-behavior-proposal` (`browser-tool`; `agent-tool` prepare): moves a draft to submitted/review-needed when backend evidence, version, provider/runtime, policy, and scope prerequisites pass.
- `approve-agent-behavior-proposal`, `reject-agent-behavior-proposal`, `defer-agent-behavior-proposal`, and `cancel-agent-behavior-proposal` (`browser-tool` human decisions): record review state with reason/acknowledgement validation and idempotent replay handling; approval does not activate behavior.
- `activate-agent-behavior-version`, `rollback-agent-behavior-version`, and `deactivate-agent-behavior-version` (`browser-tool` approval): perform explicit lifecycle changes only from approved/eligible backend states with version, scope, provider/runtime, tool-boundary, and policy checks.
- `start-agent-prompt-risk-review`, `read-agent-prompt-risk-review`, `accept-agent-prompt-risk-review`, `reject-agent-prompt-risk-review`, and `cancel-agent-prompt-risk-review` (`browser-tool`, `agent-tool` read/prepare, `internal-tool` worker): govern real model-backed prompt-risk review progress/results; blocked, deferred, fixture-only, or model-less results cannot be accepted.
- `prepare-agent-seed-import`, `start-agent-seed-import`, and `cancel-agent-seed-import` (`browser-tool`; `agent-tool` prepare): compute and execute customization-preserving seed imports with provenance, conflict, version, provider/runtime, and idempotency checks.
- `readSkill` and `readReferenceDoc` (`agent-tool` loaders): authorized by manifest, scope, and tool boundary and traced.

## Behavior lifecycle state machine

Canonical proposal states are `draft`, `submitted`, `in_review`, `approved`, `rejected`, `deferred`, `cancelled`, `ready_for_activation`, `activation_blocked`, `active`, `rollback_available`, `rollback_blocked`, `rolled_back`, and `deactivated`. Legal transitions are: draft -> submitted; submitted/in_review -> approved, rejected, deferred, or cancelled; approved -> ready_for_activation when all runtime and policy prerequisites pass; ready_for_activation -> active through activation confirmation; active -> rollback_available when a prior safe version exists; rollback_available -> rolled_back through rollback confirmation; active -> deactivated only through explicit deactivation confirmation. Repeated terminal decisions are no-op/idempotent with trace evidence. Prompt-risk review and seed-import task states are separate durable task states and may provide evidence for proposal transitions, but they do not directly activate, roll back, or expand authority.

## Authorization and denials

Backend policy, version state, approval gates, platform/tenant/customer scope, `ToolPermissionBoundary`, and provider fail-closed rules are authoritative. Prompt text, expertise text, or rail visibility cannot expand authority.

## Outcomes

In scope: active default managed agents for the five foundation workstreams, governed behavior lifecycle, compact expertise manifests, loader denials/traces, seed provenance, customization-preserving upgrades, safe model provider boundary, serious missing-provider runtime blockers, and behavior change decision cards.

Out of scope: arbitrary Java class registration from tenant content, unapproved tool expansion, preloading all skill/reference text into prompts, and model-less runtime substitutes for model-backed behavior.

## Linked graph nodes

- Workstream: `../workstreams/agent-admin/workstream.md`
- Tests: `../workstreams/agent-admin/tests/coverage.md`
- Traces: `../workstreams/agent-admin/traces/work-traces.md`
