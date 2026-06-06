# Conversation Capture: Workstream Contract Cleanup

## Review summary

The user asked how well workstreams are defined in `skills-pack/` and what is missing, needs polish, or should be tightened.

The review concluded that the conceptual model is strong:

```text
workstream definition
→ exactly one functional/context-area agent
→ role-specific dashboard and attention model
→ human surface graph with typed surfaces
→ capability-backed actions and governed-tools
→ optional internal workstream agent graph
→ Akka/API/UI realization with auth, traces, retention, and tests
```

The main weakness is not the concept; it is drift between doctrine, manifest docs, JSON schema, validator, templates, and UI examples.

## Accepted decisions

1. `managedAgentDefinitionId` is required for every workstream in `workstream-manifest.json`, including early readiness levels. If the governed record is not separately named yet, it may match `functionalAgentId`.
2. Workstream icon metadata must require explicit `tooltip` everywhere: contract, manifest docs, JSON schema, validator, and templates.
3. `attentionCategories` in the manifest are workstream-local category ids, not necessarily the canonical `AttentionItem.category` enum. Each local id must map in markdown/producer contracts to a canonical attention category, severity rules, producer, and lifecycle behavior.
4. Canonical attention severity vocabulary is `info | warning | urgent | blocked`. UI docs should replace `critical` with this vocabulary.
5. Add a lightweight machine-readable `surfaceActionMappings` / `governedToolMappings` section now. It may be optional for early readiness but is required at `capability-ready` and higher.
6. `internalWorkers` should become structured when present, with fields such as worker id, substrate, trigger, authority basis, capability id, governed-tool id, and progress/result/failure surfaces. Empty/no internal workers remain allowed.
7. Do not change skill doc references like `../docs/...` to source-layout `../../docs/...`. The skills-pack contents are lifted into `.agents/skills`, where these references resolve correctly.
8. Installed-layout reference convention is normative. Validators/review should check the installed `.agents/skills` layout, not source-layout resolution.
9. Add or update an install-layout reference check so pack verification can catch genuinely broken `../docs/...`, `../references/...`, `../examples/...`, and `../templates/...` references after install.
10. Add explicit evidence fields for `runtime-ready` and `production-ready`, while keeping lower readiness levels lightweight.
11. `surfaceActionMappings` should be required when readiness is `capability-ready`, `expertise-ready`, `runtime-ready`, or `production-ready`.
12. Structured `internalWorkers` are required only when a workstream declares internal/background worker behavior; otherwise an empty or omitted list is valid.
13. `attentionCategories: []` is valid only when markdown explicitly explains that the workstream has no actionable attention model yet or intentionally does not produce attention.
14. Create this specs mini-project before implementation and execute subsequent cleanup via one task per fresh harness session.

## Rejected alternatives / non-goals

- Do not treat source-relative skill paths as the canonical path basis.
- Do not force every workstream to have internal workers.
- Do not require full runtime evidence for early/design readiness levels below `runtime-ready`.
- Do not turn this into a root app runtime implementation project.

## Risks

- Adding schema fields may require updating templates and examples in the same task to keep checks green.
- Making mappings required at `capability-ready` and above may reveal existing templates that over-claim readiness.
- Installed-layout validation must avoid false positives for prose examples that are not intended as links.

## Unresolved questions

None currently blocking. Verification may identify additional bounded cleanup tasks.
