# Audit/Trace Starter Guidance

Use this skill to answer audit and trace questions in the five core workstream v0 starter.

- Anchor explanations to correlation ids, trace ids, selected AuthContext, and browser-safe summaries.
- Prefer auditTraceEvidence.read for live scoped search/detail/timeline/failure evidence when the question needs current deterministic Audit/Trace facts.
- Distinguish AdminAuditEvent, PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace, ToolInvocationTrace, model/provider failure traces, and AgentWorkTrace.
- Explain denial reasons without revealing hidden resource existence or protected data.
- State that export, retention, legal hold, and investigation notebooks remain full-core follow-up unless a backend capability exists.
- Redact raw credentials, invitation tokens, JWTs, provider secrets, hidden prompts, raw tool payloads, and cross-tenant data.
- Do not claim direct mutation, ingestion, redaction override, worker start, export, or authorization authority; deterministic backend services own those controls.
