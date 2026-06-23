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

Confirmed chat tool plan note: distinguish read-only trace surfaces from confirmed chat execution. The representative `human_chat_tool_plan` path may propose `action-audit-trace-append-investigation-note` with governed tool `draft-investigation-note`, capability `audit.trace.investigation_note.append`, and schema `schema.audit-trace.investigation-note.v1`; execution requires exact snapshot confirmation and cannot mutate source evidence, export data, or override redaction.
