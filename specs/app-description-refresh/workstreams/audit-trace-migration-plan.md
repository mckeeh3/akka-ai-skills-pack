# Audit/Trace Workstream Migration Plan

## Scope

Refresh `app-description/domains/core-starter/workstreams/audit-trace/**` to the current skills-pack app-description graph contract.

## Primary intent

Authorized administrators investigate audit/work traces, correlation chains, authorization denials, agent/tool/data/policy usage, provider/config fail-closed events, and runtime-validation evidence across foundation workstreams.

## Required graph coverage

- Workstream purpose and lifecycle/alignment state.
- Tenant admin human worker, Audit/Trace functional agent, audit-trace system worker.
- Surfaces for trace search, trace detail, timeline, actor/workstream filters, denial views, agent/tool traces, policy/decision evidence, export/redaction where allowed, and investigation summaries.
- Governed tools for audit search/read, work trace search/read, correlation lookup, denial investigation, agent-work trace reads, and evidence summary.
- Actor adapters: surface actions, confirmed human chat plans for read-only investigations where allowed, agent tool calls where bounded, projection/consumer/internal/API calls.
- Capability links to audit-and-trace investigation and audit/governance state.
- Tenant scope, support access visibility, redaction, retention, forbidden access behavior, and least-privilege trace visibility.
- Trace obligations for trace reads themselves, investigation summaries, denied trace access, and cross-workstream correlation.
- Tests and runtime-validation scenarios for trace search/detail, forbidden trace read, redaction, agent/tool trace evidence, and cross-workstream correlation.
- Realization files and source-alignment entries.

## Specific refresh questions for the task

- Which trace categories are visible to organization admins vs SaaS owner/support operators?
- Which exports are allowed, redacted, or deferred?
- Which runtime-validation runs should link into Audit/Trace as evidence?

## Expected task output

The task should update only Audit/Trace workstream files plus narrow shared references if required, then mark lifecycle/source-alignment to reflect description changes and implementation alignment.
