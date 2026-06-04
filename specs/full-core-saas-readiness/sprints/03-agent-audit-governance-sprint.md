# Sprint 03: Managed-Agent, Audit/Trace, and Governance/Policy Depth

## Objective

Close the core AI/governance readiness gaps for managed runtime agents, audit investigation, and policy governance.

## Scope

- AgentDefinition lifecycle and Agent Admin catalog/detail surfaces.
- Prompt/skill/reference documents and versions, manifests, tool boundaries, proposals, approvals, activation/rollback, and traces.
- Audit/Trace searchable investigation surfaces with scoped redaction/export rules.
- Governance/Policy proposal, simulation/impact, review, approval, activation/rollback, and outcome surfaces.

## Acceptance criteria

- Prompt/skill/reference text cannot grant authority.
- ToolPermissionBoundary and backend capabilities remain authoritative.
- PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace, AgentWorkTrace, admin audit, and workstream traces are emitted and searchable as required.
- High-impact behavior/policy changes require human review/approval.
