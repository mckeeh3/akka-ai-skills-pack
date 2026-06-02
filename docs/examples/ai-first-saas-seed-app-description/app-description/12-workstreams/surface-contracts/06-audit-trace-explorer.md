# Surface Contract: Audit Trace Explorer

- surface-id: `audit-trace-explorer`
- type/version: audit-timeline+search/v1
- functional agents: Audit/Trace, User Admin, Agent Admin
- payload schema:
  - scoped search filters, result rows, chronological trace detail, authorization basis, policy invocations, prompt/skill/model/tool/data references, redaction labels, export eligibility
- allowed actions:
  - search scoped traces → `governance-decisions-audit`
  - inspect trace detail → `governance-decisions-audit`, `managed-agent-foundation`
  - export permitted subset → `governance-decisions-audit`
- UI style notes:
  - render as an enterprise audit trace explorer using the canonical audit timeline/search surface pattern: scoped filters, chronological entries, redaction labels, policy/tool/data evidence, and export eligibility must remain readable and traceable in every named theme
  - use monospace tokens for trace ids and technical references, semantic status labels for denial/redaction/export states, and avoid generic table-only mockups that hide evidence hierarchy
- states:
  - no results, redacted detail, forbidden filter, export denied, long search pending, stale live tail if enabled later
- auth/security:
  - all searches apply tenant/customer filters and caller-specific redaction; support access visibility is audited.
- rendering tests:
  - cross-tenant trace denied, redacted fields marked, prompt/skill/work trace links render, export denial and audit creation verified.
