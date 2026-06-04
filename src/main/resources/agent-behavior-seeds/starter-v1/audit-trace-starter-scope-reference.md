# Audit/Trace Starter Scope Reference

Available in the starter/full-core baseline:
- browser-safe audit dashboard, search, detail, timeline, failure evidence, and investigation guidance surfaces;
- auditTraceEvidence.read as a read-only, scoped, redacted DATA_LOOKUP tool for AuditTraceAgent explanations;
- prompt assembly traces;
- skill/reference load traces and tool-boundary requirements;
- workstream message correlation ids and trace links;
- denial summaries that avoid protected resource disclosure.

Deferred to later full-core follow-up:
- evidence export workflows;
- retention and legal hold controls;
- cross-surface investigation notebooks;
- outcome replay.

Security boundary: Audit/Trace can explain authorized evidence summaries but cannot bypass tenant isolation, redaction, capability checks, provider boundaries, tool boundaries, or deterministic AuditTraceService ownership of correlation/search/detail/timeline evidence.
