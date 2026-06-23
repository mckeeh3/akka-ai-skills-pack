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

Confirmed chat tool plan reference: Audit/Trace now has an expanded chat plan catalog covering two classification categories:

- Chat-executable-now read paths (tenant/customer-scoped, redacted, visible trace/correlation/filter binding required): `action-audit-trace-search` / `search-audit-traces` / `audit.trace.search`; `action-audit-trace-detail` / `read-trace-detail` / `audit.trace.detail.read`; `action-audit-trace-timeline` / `read-trace-timeline` / `audit.trace.timeline.read`; `action-audit-trace-failure-evidence` / `read-trace-failure-evidence` / `audit.trace.failureEvidence.read`; `action-audit-trace-investigation-guide` / `read-investigation-guide` / `audit.trace.investigationGuide.read`. Hidden trace enumeration is denied; export authority is not granted.

- Chat-executable-now append (exact snapshot confirmation, no source evidence mutation): `action-audit-trace-append-investigation-note` / `draft-investigation-note` / `audit.trace.investigation_note.append` / `schema.audit-trace.investigation-note.v1`. Appends only a human-authored, browser-safe note; source evidence, exports, retention, and redaction remain separately governed.

Routed Audit/Trace surfaces are navigation and review aids for all non-catalog paths. Summary progress/review and export request surfaces remain structured-surface responsibilities outside the chat catalog.
