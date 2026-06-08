# Production-Ready Five Core v0 App

## Goal

Turn the five-core workstream v0 starter from a validated scaffold/demo slice into a production-ready local Akka application slice. Local execution on Akka is treated as production-like validation: no fake chatbot, no deterministic pretend model response, no fixture-only success path, and no ambiguous "kinda/sorta implemented" claims.

The first real v0 app must prove this end-to-end path:

```text
WorkOS/AuthKit-authenticated bootstrap user
→ selected AuthContext
→ five role-authorized core workstreams in the left rail
→ selected real governed workstream agent
→ active prompt/skill/reference/model config resolved from governed storage
→ real AI model invocation through backend-only provider configuration
→ durable workstream request/response entries
→ markdown_response surface rendered as sanitized HTML
→ prompt/model/tool/work/audit traces
→ tests and local smoke validation
```

## Production-ready v0 standard

A feature is not done because the UI looks plausible or a deterministic seam returns text. It is done only when the named local runtime behavior works through the real intended surface with backend authorization, durable state where required, real provider integration where required, traces, tests, and documented operational setup.

For this queue, `production-ready v0` means:

- real model-backed workstream agent responses are required;
- deterministic/demo responses may remain only as test doubles inside tests, never as the normal runtime path;
- missing provider configuration blocks model-backed message submission with a safe actionable error;
- workstream entries and traces are durable enough for local restart/replay validation;
- the five initial core workstreams all use the same real message path;
- docs, prompts, and skills must aggressively route agents toward production-ready implementation instead of mock/simulate/defer defaults.

## Release handoff

Final validation results and the recommended local trial process are recorded in `release-handoff.md`.

## Non-goals

- Do not complete every full-core SaaS surface in this queue.
- Do not add app-specific domain workstreams.
- Do not make frontend-only authorization or prompt text grant authority.
- Do not put provider secrets in frontend files or built static assets.
