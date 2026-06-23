# Audit/Trace Starter Guidance

Use this skill to answer audit and trace questions in the five core workstream starter.

- Anchor explanations to correlation ids, trace ids, selected AuthContext, and browser-safe summaries.
- Prefer auditTraceEvidence.read for live scoped search/detail/timeline/failure evidence when the question needs current deterministic Audit/Trace facts.
- Describe Audit/Trace as a role-specific dashboard and human surface graph over trace attention, denials, and evidence; surface actions must name their governed-tool and qualified browser-tool/agent-tool/internal-tool exposure when explaining what can be opened, searched, or followed up.
- Be familiar with key surfaces: dashboard for scoped counters and attention, trace search, trace detail, correlation timeline, failure evidence, investigation guide, export request, investigation note, summary progress/review, and system-message recovery.
- Distinguish AdminAuditEvent, PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace, ToolInvocationTrace, model/provider failure traces, and AgentWorkTrace.
- Explain denial reasons without revealing hidden resource existence or protected data.
- State that export, retention, legal hold, and investigation notebooks remain full-core follow-up unless a backend capability exists.
- Redact raw credentials, invitation tokens, JWTs, provider secrets, hidden prompts, raw governed-tool payloads, and cross-tenant data.
- Route export, note append, summary start/review, retention, legal hold, and redaction-change asks to structured surfaces or safe fallback; do not claim direct mutation, ingestion, redaction override, worker start, export, or authorization authority because deterministic backend services own those controls.

Confirmed chat tool plan note: distinguish read-only trace surfaces from confirmed chat execution. The expanded `human_chat_tool_plan` catalog now covers two categories:

1. Chat-executable-now read paths (no mutation, tenant/customer scoped, redacted, visible trace/correlation/filter binding required, hidden trace enumeration denied): `action-audit-trace-search` / `search-audit-traces` / `audit.trace.search`; `action-audit-trace-detail` / `read-trace-detail` / `audit.trace.detail.read`; `action-audit-trace-timeline` / `read-trace-timeline` / `audit.trace.timeline.read`; `action-audit-trace-failure-evidence` / `read-trace-failure-evidence` / `audit.trace.failureEvidence.read`; `action-audit-trace-investigation-guide` / `read-investigation-guide` / `audit.trace.investigationGuide.read`. Export, raw evidence text, and cross-tenant filters are denied.

2. Chat-executable-now append path (exact snapshot confirmation, idempotency, no source evidence mutation): `action-audit-trace-append-investigation-note` / `draft-investigation-note` / `audit.trace.investigation_note.append` / `schema.audit-trace.investigation-note.v1`. Confirmation cannot mutate source evidence, export data, or override redaction. Summary and safe fallback routes remain for surface guidance; export and raw evidence paths remain out of catalog.
