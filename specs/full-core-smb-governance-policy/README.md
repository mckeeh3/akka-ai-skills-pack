# Full-Core SMB Governance/Policy

## Purpose

Make Governance/Policy the next SMB full-core workstream in the AI-first SaaS starter.

Governance/Policy should become the SMB control center for active policies, thresholds, proposals, approvals, exceptions, simulations, behavior-authority changes, decisions, and outcome traces.

This mini-project targets `templates/ai-first-saas-starter/` as the executable baseline and preserves the workstream + structured surface architecture.

## Background

Completed predecessor full-core slices now provide the foundations Governance/Policy needs:

- `specs/full-core-smb-user-admin/` and follow-up User Admin mini-projects provide access, role, invitation, member-status, agent-guidance, and access-review evidence.
- `specs/full-core-smb-agent-admin/` provides managed-agent visibility, behavior-change lifecycle, tool-boundary/model/provider evidence, and AgentAdminAgent guidance.
- `specs/full-core-smb-audit-trace/` exposes searchable, redacted investigation evidence and AuditTraceAgent explanations.
- `specs/full-core-smb-baseline-and-ux/` defines shared workstream shell, structured surface, system-message, runtime validation, and visual quality contracts.

Governance/Policy is now positioned to use those foundations for policy proposals, simulations, approvals, exceptions, and decision cards without inventing evidence paths.

## Scope

Full-core SMB Governance/Policy should cover these vertical slices, appended as bounded tasks after source inspection:

1. **Governance dashboard and policy inventory/detail foundations**: active policies, capability gates, thresholds, owners, versions, posture, pending approvals, exceptions, and trace links.
2. **Proposal draft/submit/read lifecycle**: deterministic proposal state for policy or authority-boundary changes with validation, idempotency, audit, and safe browser DTOs.
3. **Simulation/replay evidence surface**: deterministic simulation over scoped evidence where available, with expected allows/denials, affected capabilities/artifacts, warnings, and trace links.
4. **Approve/reject/activate/rollback decision cards**: human-authorized decision lifecycle with idempotency, activation/rollback semantics, outcome traces, and no model-owned authority.
5. **GovernancePolicyAgent request/response guidance**: governed Akka `Agent` runtime for explanations, proposal drafting help, simulation interpretation, and provider fail-closed surfaces.
6. **Policy-impact analysis worker candidate**: durable internal worker only after deterministic proposal/simulation foundations exist and lifecycle semantics justify it.

## Non-goals

- Do not implement enterprise compliance frameworks, policy-as-code authoring suites, SIEM, legal hold, or governance-office workflow suites.
- Do not reimplement User Admin, Agent Admin, or Audit/Trace inside Governance/Policy; consume their safe evidence and trace links.
- Do not expose secrets, hidden prompts, raw policy internals that are not browser-safe, provider credentials, or cross-tenant evidence.
- Do not let AI own authorization, policy evaluation, approval, activation, rollback, simulation normalization, idempotency, or trace redaction.
- Do not use deterministic/model-less normal runtime behavior as a substitute for model-backed GovernancePolicyAgent or worker behavior.
- Do not allow fully autonomous policy activation in this SMB slice.

## Target source areas

Primary executable baseline:

- `templates/ai-first-saas-starter/`

Likely source families to inspect before editing:

- governance/policy services, workstream services, and structured surface DTOs under backend application packages;
- agentfoundation managed-agent runtime, tools, tool-boundary, seed, and trace services;
- User Admin, Agent Admin, and Audit/Trace evidence/link source areas already implemented;
- seed material under `templates/ai-first-saas-starter/backend/src/main/resources/agent-behavior-seeds/starter-v1/`;
- frontend workstream shell, surfaces, fixtures, actions, decision-card rendering, and contract tests under `templates/ai-first-saas-starter/frontend/src/`;
- broad starter validation script and scaffold path.

## Execution model

Execute one task per fresh harness session. Start with vertical contracts and a source-boundary implementation map. The first implementation-map task must append bounded source-edit tasks and task briefs before runtime implementation begins.

## Read order for future task sessions

1. `AGENTS.md`
2. this mini-project `README.md`
3. `conversation-capture.md`
4. selected sprint and backlog files
5. selected task brief
6. predecessor SMB/User Admin/Agent Admin/Audit Trace contracts named by the task
7. smallest listed skill files and discovered source files

## Done state

This mini-project is complete when Governance/Policy has an SMB-ready full-core vertical at the implemented scope:

- authorized operators can open a governance dashboard with active policy posture, pending approvals, exceptions, recent decisions, blocked changes, and trace links;
- policy inventory/detail reads are capability-checked, tenant-scoped, redacted, versioned, and trace-linked;
- proposal draft/submit/read and approve/reject/activate/rollback behavior is deterministic, idempotent, audited, and safe;
- simulation/replay evidence is deterministic and does not grant authority;
- GovernancePolicyAgent uses the governed Akka Agent runtime path and fails closed when provider/model config is absent;
- any internal policy-impact worker uses durable task semantics and cannot bypass deterministic policy authorization/activation;
- frontend surfaces are visually polished, structured, accessible, and explicit about provider/config/authorization denials;
- targeted backend/frontend tests and broad starter validation pass, or remaining blockers are captured as bounded follow-up tasks.
