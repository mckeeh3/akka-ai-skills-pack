# Workstreams and Retention

## Workstream semantics

- One durable workstream exists per functional agent and selected `AuthContext` scope unless a capability explicitly creates a goal-, decision-, or audit-focused subthread.
- Workstreams contain user requests, agent responses, capability results, structured surfaces, workflow progress, decision cards, policy citations, trace links, safe denials, and follow-up actions.
- Composer input is contextual to the selected functional agent and must resolve to a governed capability, evidence request, proposal, approval request, or explicit refusal.
- Surface updates append or replace typed surface payloads; consequential state changes occur only through backend capabilities.

## Required continuity fields

- `tenantId`, optional `customerId`, `accountId`, `membershipId`, roles/capabilities snapshot
- `functionalAgentId`, optional `internalAgentId`, `workstreamId`, `surfaceId`, `capabilityId`
- `correlationId`, `causationId`, idempotency key where side effects are possible
- trace references: AdminAuditEvent, PromptAssemblyTrace, SkillLoadTrace, AgentWorkTrace, DecisionTrace, PolicyInvocation, ToolInvocation, DataAccessEvent

## Retention and summarization

- Security, authorization, support-access, billing-boundary, governed-agent, approval, and consequential AI/tool traces use audit-grade retention defined by auth/security and observability layers.
- Workstream summaries may compress routine activity for UX, but must preserve links to full audit/work traces.
- Redaction must be applied before returning workstream history to the browser or an agent tool.
- Deleted or disabled accounts do not remove audit-required records; UI renders safe actor labels according to policy.
