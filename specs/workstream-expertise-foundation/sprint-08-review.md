# Sprint 08 Review: Workstream Model Binding Readiness

## Scope reviewed

Reviewed the Sprint 08 objective/backlog, the pre-sprint model-binding readiness review, canonical workstream expertise doctrine, agent coverage matrix, model-governance and readiness skills, seed workstream expertise bundle contracts, and starter runtime/test coverage for governed model binding.

## Outcome

Sprint 08 is complete. Workstream expertise is now ready for generated apps to model each LLM-backed workstream as a workstream-specific governed agent with an explicit approved model binding or an explicitly inherited governed default.

The pre-sprint gap was that model choice could remain implicit even when prompts, skills, references, manifests, tools, traces, and tests were modeled. That gap is closed across doctrine, app-description contracts, starter runtime coverage, planning/generation guidance, readiness gates, and coverage reporting.

## Evidence by deliverable

| Deliverable | Status | Evidence |
|---|---|---|
| Expert bundle contract includes model binding | Complete | `docs/workstream-expertise-model.md` now requires `ModelConfigRef`/`ModelPolicy` or explicit inherited governed default, allowed modes, fallback/no-fallback policy, provider secret boundary, model-use traces, and readiness blocking for missing bindings. |
| Seed app-description bundles name bindings | Complete | All foundation bundles under `docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/workstream-expertise/` name an inherited governed default model binding and runtime/provider-secret/test obligations. |
| Starter runtime validates governed model binding before invocation | Complete | `AgentRuntimeService.activeModelBinding(...)` resolves tenant-scoped `ModelConfigRef`/`ModelPolicy`, denies disabled/unknown/unauthorized/policy-denied/secret-like bindings, requires explicit fallback policy when fallback is enabled, and renders safe model refs in prompt assembly trace summaries. |
| Starter tests prove model governance | Complete | `AgentRuntimeServiceTest` covers active model success, disabled/unknown/cross-scope/unauthorized model denial, provider-policy denial, explicit fallback success, missing fallback denial, safe trace refs, and provider-secret non-exposure. Seed/state tests are recorded in TASK-WEF-08-003 and TASK-WEF-08-004 notes. |
| Planning, generation, and readiness require model binding | Complete | `skills/akka-prd-to-specs-backlog/SKILL.md`, `skills/app-generate-app/SKILL.md`, and `skills/app-description-readiness-assessment/SKILL.md` require approved model binding or explicit inherited governed default for LLM-backed workstream agents. |
| Coverage matrix updated | Complete | `docs/agent-coverage-matrix.md` marks governed model configuration as covered with starter `ModelConfigRef`, `ModelPolicy`, `AgentRuntimeService.activeModelBinding(...)`, and targeted tests. |

## Answer to review question

Yes: the workstream expertise foundation is ready for generated apps with workstream-specific agents and governed model binding.

Readiness here means the skills pack now has enough doctrine, routing, planning, app-description contract, generation gate, starter runtime reference, and tests to prevent a generated LLM-backed workstream agent from being declared ready with an implicit or prompt-selected model. A generated app still must define its own domain-specific expert bundles and model bindings when it adds domain-specific workstreams, but the required tasks and readiness gates now make that work explicit.

## Remaining gaps

No Sprint 08 blocking gaps remain.

Non-blocking future cleanup remains tracked outside this workstream in the general agent coverage cleanup backlog, such as custom model provider examples and broader non-component facade tests. Those do not block the workstream expertise/model-binding readiness claim.

## Required check evidence

- `git diff --check`: passed.
- Text-search proof reviewed model-binding coverage across doctrine, seed bundles, planning/generation/readiness skills, coverage matrix, starter runtime, and runtime tests. Evidence terms included `ModelConfigRef`, `ModelPolicy`, `inherited governed default model binding`, `provider secret boundary`, `modelConfigRef=starter-default-model`, `fallback=noFallback`, and `model-provider-denied`.

## Pending task adjustments

No additional workstream-expertise sprint is required. The workstream expertise foundation now covers workstream-specific functional agents, governed prompts, approved model bindings, skills, reference documents, compact manifests, tool boundaries, authorized loaders, traces, planning/generation gates, and tests.
