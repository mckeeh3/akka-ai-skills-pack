# Workstreams and Retention

## Workstream definition vs runtime instance

A workstream definition is the product vertical such as `user-admin`. A workstream instance is the durable runtime timeline/log for a selected scope such as `tenantId + selectedContextId + functionalAgentId` and optional customer/subthread keys when a capability explicitly creates them.

## Workstream semantics

- One workstream definition exists per user-facing functional/context-area agent.
- One durable workstream instance exists per functional agent and selected `AuthContext` scope unless a capability explicitly creates a goal-, decision-, audit-, or customer-focused subthread.
- Workstream instances contain user requests, surface requests, agent responses, capability results, structured surfaces, workflow progress, decision cards, policy citations, trace links, safe denials, and follow-up actions.
- Surface updates append or replace typed payloads; consequential state changes occur only through backend capabilities and governed-tools.
- My Account may aggregate authorized attention from other workstreams, but it does not own their source items.

## Required continuity fields

- `workstreamId`, `workstreamInstanceId`, `functionalAgentId`, optional `internalAgentId`
- `tenantId`, optional `customerId`, `selectedContextId`, `accountId`, `membershipId`, role/capability snapshot, support-access status
- `surfaceId`, `capabilityId`, `governedToolId`, and exposure channel where applicable
- `correlationId`, optional `causationId`, idempotency key for side effects or replay-sensitive projections
- trace refs: AdminAuditEvent, PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace, AgentWorkTrace, DecisionTrace, PolicyInvocation, ToolInvocation, DataAccessEvent

## Retention and summarization

- Security, authorization, support-access, governed-agent, approval, consequential AI/tool, and protected data-access traces use audit-grade retention.
- Routine workstream summaries may compress UX history, but must preserve links to full audit/work traces.
- Redaction is applied before returning workstream history to browsers, agents, exports, or support contexts.
- Deleted or disabled accounts do not remove audit-required records; UI renders safe actor labels according to policy.
- Provider/security failures are retained as safe system-message surfaces and trace records, not as silent missing responses.

## Readiness labels

Use readiness labels from `docs/workstream-contract.md`: `identified`, `described`, `surface-ready`, `capability-ready`, `expertise-ready`, `runtime-ready`, and `production-ready`. Do not mark a workstream runtime-ready from fixtures, static markdown, or provider-bypassing service calls.
