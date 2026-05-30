# Full-Core SMB Audit/Trace

## Purpose

Make Audit/Trace the next SMB full-core workstream in the AI-first SaaS starter.

Audit/Trace should become the practical investigation workspace for who, what, when, why, and how-authorized across identity, User Admin changes, Agent Admin behavior changes, model/tool traces, worker tasks, provider failures, denials, timelines, and trace-linked explanations.

This mini-project targets `templates/ai-first-saas-starter/` as the executable baseline and preserves the workstream + structured surface architecture.

## Background

Completed predecessor full-core slices now produce meaningful trace evidence:

- `specs/full-core-smb-user-admin/` — User Admin dashboard and invitation foundation;
- `specs/full-core-smb-user-admin-access-management/` — member status and role/capability changes;
- `specs/full-core-smb-user-admin-agent-guidance/` — UserAdminAgent prompt/skill/reference/tool/model traces and provider blocks;
- `specs/full-core-smb-user-admin-access-review-worker/` — durable access-review task lifecycle traces;
- `specs/full-core-smb-agent-admin/` — Agent Admin managed-agent reads, behavior-change lifecycle, tool-boundary/evidence reads, provider readiness, and AgentAdminAgent guidance;
- `specs/full-core-smb-baseline-and-ux/` — shared workstream shell, structured surface, system-message, runtime validation, and visual quality contracts.

Audit/Trace should precede Governance/Policy full-core hardening because policy proposals, simulations, exceptions, approvals, and decisions need accessible trace evidence.

## Scope

Full-core SMB Audit/Trace should cover these vertical slices, appended as bounded tasks after source inspection:

1. **Audit dashboard and trace search/detail/timeline foundations**: scoped search, health/attention cards, correlation timeline, detail cards, filters, and trace links.
2. **Redacted event/evidence cards**: safe browser DTOs for authorization denials, commands, reads, no-ops, provider blocks, model/tool traces, worker lifecycle, and behavior changes.
3. **Failure evidence surfaces**: provider/tool/model/worker failure evidence, denial reasons, recovery guidance, and related workstream links.
4. **Cross-workstream trace links**: User Admin and Agent Admin surfaces should open Audit/Trace detail/timeline where authorized without leaking hidden traces.
5. **AuditTraceAgent request/response guidance**: governed Akka `Agent` runtime for trace explanations, summaries, investigative next steps, and provider fail-closed surfaces.
6. **Scheduled audit-summary worker candidate**: durable internal worker only after deterministic trace search/detail foundations exist and lifecycle semantics justify it.

## Non-goals

- Do not implement SIEM integrations, legal hold, e-discovery, complex retention policy consoles, or enterprise compliance-suite features.
- Do not expose raw secrets, hidden prompts, unauthorized prompt text, provider credentials, cross-tenant evidence, or unredacted payloads.
- Do not use AI to own trace ingestion, authorization, tenant filtering, redaction, retention, or deterministic correlation.
- Do not use deterministic/model-less normal runtime behavior as a substitute for model-backed AuditTraceAgent or worker behavior.
- Do not expand into Governance/Policy full-core implementation beyond trace/evidence hooks needed later.

## Target source areas

Primary executable baseline:

- `templates/ai-first-saas-starter/`

Likely source families to inspect before editing:

- trace/audit records and projection services under backend application packages;
- workstream services and structured surface DTOs under backend application/workstream/security packages;
- governed agent runtime and tool-boundary services under backend `application/agentfoundation/`;
- User Admin and Agent Admin trace-link source areas already implemented;
- frontend workstream shell, surfaces, fixtures, actions, trace/timeline rendering, and contract tests under `templates/ai-first-saas-starter/frontend/src/`;
- broad starter validation script and scaffold path.

## Execution model

Execute one task per fresh harness session. Start with vertical contracts and a source-boundary implementation map. The first implementation-map task must append bounded source-edit tasks and task briefs before runtime implementation begins.

## Read order for future task sessions

1. `AGENTS.md`
2. this mini-project `README.md`
3. `conversation-capture.md`
4. selected sprint and backlog files
5. selected task brief
6. predecessor SMB/User Admin/Agent Admin contracts named by the task
7. smallest listed skill files and discovered source files

## Done state

This mini-project is complete when Audit/Trace has an SMB-ready full-core vertical at the implemented scope:

- authorized operators can open an Audit/Trace dashboard with meaningful trace health, denials, provider/tool failures, recent admin/behavior changes, and investigation shortcuts;
- trace search/detail/timeline reads are capability-checked, tenant-scoped, redacted, correlated, and trace-linked;
- event/evidence cards safely explain authorization basis, denial reason, provider/model/tool/worker failures, behavior changes, and related workstream actions;
- User Admin and Agent Admin trace links route through authorized Audit/Trace capabilities without leaking hidden evidence;
- AuditTraceAgent uses the governed Akka Agent runtime path and fails closed when provider/model config is absent;
- any internal audit-summary worker uses durable task semantics and cannot bypass deterministic trace authorization/redaction;
- frontend surfaces are visually polished, structured, accessible, and explicit about provider/config/authorization denials;
- targeted backend/frontend tests and broad starter validation pass, or remaining blockers are captured as bounded follow-up tasks.
