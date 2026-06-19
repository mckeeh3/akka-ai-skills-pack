# Behavior: Audit/Trace

## Current-state behavior

Search, inspect, explain, redact, summarize, export, and annotate audit/work trace evidence for the selected scope. The workstream starts from a role-specific dashboard, accepts contextual composer requests, returns structured surfaces, and maps consequential actions to governed backend tools. Audit/work trace records are append-only; correction or cleanup is modeled as linked redaction, supersession, investigation-note, trace-gap, export-expiry, or outcome records rather than mutation of original evidence.

## Agent behavior

`audit-trace-agent` may explain, summarize, draft, recommend, and prepare proposals only within authorized capabilities. It cannot grant permissions through prompt text, bypass approval gates, or act outside its tool boundary. Model-backed turns use governed runtime configuration or fail closed.

## Edge cases

Repeated commands must be idempotent where side-effecting; stale data returns a stale/reconnect or conflict state; provider/security misconfiguration returns actionable denial/failure feedback; unsupported business-domain requests are routed to extension guidance rather than silently added. Hidden/cross-scope evidence, raw secret/provider data, unredacted export by default, unsupported authority expansion, provider/model unavailable summary tasks, stale investigation targets, expired export downloads, unauthorized note edits, and unresolved trace gaps are denied or fail closed with traceable recovery. Generated export bundles are short-lived browser artifacts and expiring them never deletes source audit evidence. Accepted investigation notes are versioned; edits create superseding notes and rejected/redacted notes remain visible only where authorized.
