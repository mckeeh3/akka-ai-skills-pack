# _workstream source alignment

Lifecycle: ../lifecycle.md
Last reviewed: unknown
Alignment state: not-started

This file maps feature-bearing app-description files to the source, frontend, API, test, spec, and runtime-validation artifacts that realize them. Keep it consistent with the owning workstream lifecycle record.

## Alignment entries

| Entry id | App-description files | Implementation files | Test / validation files | Last aligned evidence | Notes |
| --- | --- | --- | --- | --- | --- |
| `_workstream.initial` | `app-description/domains/_domain/workstreams/_workstream/workstream.md`, `app-description/domains/_domain/workstreams/_workstream/behavior.md` | not-started | not-started | not-started | Replace with concrete source/test mappings when the first implementation slice is planned or generated. |

## Unmapped current-intent files

- `app-description/domains/_domain/workstreams/_workstream/**` — reason: initial bootstrap; refine into entry-specific mappings before claiming implementation alignment.

## Unmapped implementation files

- none recorded yet.

## Alignment notes

- If a mapped app-description file becomes newer than mapped implementation/test files, default the workstream lifecycle to `stale-description-changed` unless a no-code-impact review is recorded.
- If mapped implementation files change without app-description reconciliation, default to `stale-code-changed` or `partially-aligned`.
- Do not use timestamp alignment as runtime-readiness evidence; runtime readiness still requires automated checks and the real local API/UI/agent path where applicable.
