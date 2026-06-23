# Audit/Trace Starter Scope Reference

Available in the starter/full-core baseline:
- browser-safe audit dashboard, search, detail, timeline, failure evidence, investigation guidance, export request, investigation note, summary progress/review, and system-message recovery surfaces;
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

Surface routing note: routed Audit/Trace surfaces are navigation and review aids. Opening search/detail/timeline/failure evidence does not export, append notes, start summaries, change retention, ingest evidence, or override redaction; those require protected backend actions.

Security boundary: Audit/Trace can explain authorized evidence summaries but cannot bypass tenant isolation, redaction, capability checks, provider boundaries, tool boundaries, or deterministic AuditTraceService ownership of correlation/search/detail/timeline evidence.

Confirmed chat tool plan reference: Audit/Trace now has a representative chat plan path for `action-audit-trace-append-investigation-note` / `draft-investigation-note` / `audit.trace.investigation_note.append` / `schema.audit-trace.investigation-note.v1`. It can append only a human-authored, browser-safe note after confirmation; source evidence, exports, retention, and redaction remain separately governed.
