---
name: akka-entity-type-selection
description: Decide whether an Akka Java SDK stateful use case should be implemented as an EventSourcedEntity or a KeyValueEntity, then load the matching skill suite. Use only when the task is already narrowed to a stateful component and the entity type is not yet fixed.
---

# Akka Entity Type Selection

Use this skill only when the task is already narrowed to a stateful component but the user has not yet chosen between:
- `EventSourcedEntity`
- `KeyValueEntity`

This is not the general front door for broad requirements, prompts, PDRs, or specification files.
If the broader Akka component set or backend capability contract is still unknown, start with:
- `capability-first-backend`
- `akka-solution-decomposition`

## Required reading

Read these first if present:
- `akka-context/sdk/event-sourced-entities.html.md`
- `akka-context/sdk/key-value-entities.html.md`
- `akka-context/sdk/ai-coding-assistant-guidelines.html.md`
- `../docs/capability-first-backend-architecture.md` when the stateful object implements a named capability with auth/scope, idempotency, audit, approval, or exposure-surface choices
- `../docs/ai-first-saas-application-architecture.md` when the stateful object represents goals, plans, policies, decisions, approvals, traces, outcomes, agent authority, or other AI-first substrate state
- `../references/akka-entity-comparison.md`

## Decision rule

Choose the simplest model that preserves the required business semantics.

For capability-first work, decide which named capability the entity will carry, where AuthContext/scope is enforced, whether duplicate commands must be idempotent no-ops, what audit/trace record is required, and whether any command/read should be exposed through an endpoint, workflow, or agent tool.

For AI-first SaaS objects, decide whether the state is an audit-grade fact stream or a replaceable current-state record before considering CRUD convenience.

### Prefer Event Sourced Entity when
- history of changes matters
- downstream consumers need fact-level event streams
- one command may emit several business facts
- replay/audit/debug value matters
- the user talks about events explicitly
- views or integrations are naturally driven by durable domain events
- AI-first authority, policy, approval, decision, exception, trace, tool/data-access, or outcome facts must be explainable over time
- governance changes require provenance, replay, simulation, or comparison across versions

Repository examples:
- `AgentDefinitionEntity`
- `PromptDocumentEntity`

### Prefer Key Value Entity when
- only latest state matters
- audit/history is not required
- each successful write can be represented as replacing current state
- the domain is simpler as snapshots than as events
- the user wants a simpler CRUD-like state model with strong consistency
- AI-first state is a replaceable snapshot such as current preferences, cached supervisor filters, non-audit operational cursors, or draft working data whose history is captured elsewhere

Repository examples:
- `DurableIdentityRepositoryEntity`
- `PurchasePromptDocumentEntity`

## Important comparison points

### Storage model
- ESE: persist events, rebuild state from event history
- KVE: persist latest state only

### Domain helpers
- ESE: command helpers usually return 0..N events
- KVE: command helpers usually return updated state or no-op

### Replay logic
- ESE: needs pure `applyEvent`
- KVE: no event application step

### Teaching examples
- ESE is better when demonstrating event semantics, final events before delete, or multi-event facts
- KVE is better when demonstrating direct state replacement, simpler update flows, or snapshot-style thinking

## AI-first audit-grade heuristics

Prefer Event Sourced Entity for durable AI-first substrate objects when any of these are material to product behavior or accountability:
- `Goal`, `ExecutionPlan`, `PolicyDocument`, `PolicyClause`, `ApprovalRequest`, `Decision`, `Exception`, `Escalation`, `AuditEvent`, `WorkTrace`, `DecisionTrace`, `PolicyInvocation`, `OutcomeLink`, `PolicyCommit`, `ReplayResult`, or `SimulationResult`
- human override history or precedent formation matters
- later UI/API surfaces must answer "why was this allowed, recommended, approved, overridden, or learned?"
- downstream views/consumers need individual facts for command centers, audit search, outcome reporting, or notifications

Prefer Key Value Entity when the AI-first object is current-state only and accountability is either not required or provided by another event-sourced trace, for example current agent configuration draft, transient UI supervision preferences, idempotency records, or latest non-authoritative summary.

If the object mixes audit-grade facts with replaceable current state, split responsibilities instead of forcing one entity to do both.

## Load the matching suite

If you choose event sourced, load:
- `akka-event-sourced-entities`

If you choose key value, load:
- `akka-key-value-entities`

Then load the focused companion skills for domain, application, testing, TTL, notifications, replication, or flow patterns.

## Response pattern

When deciding, state explicitly:
1. chosen entity type
2. why the other type is less suitable here
3. whether history/audit/replay is required, including any AI-first authority, policy, decision, trace, or outcome obligation
4. how command/query capability semantics, idempotency, auth/scope, audit, and exposure choices affect the implementation
5. which skill suite should be loaded next

## Anti-patterns

Avoid:
- choosing ESE by default when latest-state semantics are enough
- choosing KVE when durable fact history is central to the business model
- choosing KVE for AI-first decisions, approvals, policy commits, or traces that must be explained or replayed later
- mixing event-sourced vocabulary into KVE implementations
- flattening a clearly event-driven process into snapshots without justification
- treating entity method exposure as automatic API or agent-tool exposure instead of a selected capability surface
