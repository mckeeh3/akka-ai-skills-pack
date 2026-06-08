# Conversation Capture: Intent Compiler Realignment

## Core decisions

- The skills pack should be understood as an **intent compiler**.
- Its source language is incremental human intent.
- Its outputs are current non-code intent artifacts and generated functional app code.
- Canonical intent documentation should describe the current intended system, not historical clutter.
- Git commits/history should carry the what/why of changes over time.
- Traceability is the structural spine: app objective -> domain -> workstream -> surface/agent/tool -> Akka component -> test -> runtime trace/outcome.
- Workstreams are the central operational unit where access, surfaces, agents, tools, policies, traces, realization, and tests become meaningful.
- Reusable artifacts should have one canonical global definition plus explicit workstream-specific bindings.
- Directory names should identify artifact types, and files should identify concrete artifact instances.

## Proposed app-description direction

The current working model is:

```text
app-description/
  app.md
  global/
    actors/<actor>.md
    roles/<role>.md
    policies/<policy>.md
    surfaces/<surface-pattern>.md
    agents/<agent>.md
    tools/<tool>.md
    traces/<trace-pattern>.md
  domains/<domain>/
    domain.md
    capabilities/<capability>.md
    data-state/<state-object>.md
    workstreams/<workstream>/
      workstream.md
      access.md
      behavior.md
      surfaces/<surface-instance-or-binding>.md
      agents/<agent-binding-or-local-agent>.md
      tools/<tool-binding-or-local-tool>.md
      policies/<policy-binding-or-local-policy>.md
      traces/<trace-binding-or-local-trace>.md
      tests/<test-expectation>.md
      realization/akka-components.md
      realization/frontend-routes.md
      realization/api-contracts.md
```

## Archive/rebuild direction

The user agreed that the existing intent-processing docs have gone through many iterations and likely contain bloat/redundancy. The current direction is to archive both old docs and old intent-processing skills selectively, then build a clean active intent-compiler layer. Focused Akka implementation skills should remain active unless they need reference/routing updates.

## Candidate intent-processing skills mentioned

- `app-description-*`
- `app-descriptions`
- `app-generate-app`
- `akka-prd-to-specs-backlog`
- `akka-revised-prd-reconciliation`
- `akka-change-request-to-spec-update`
- `akka-slice-spec-to-backlog`
- `akka-backlog-to-pending-tasks`
- `akka-backlog-item-to-task-brief`
- `akka-pending-question-*`
- `akka-do-next-pending-question`
- `akka-do-next-pending-task`
- `project-discussed-idea-to-pending-project`
- high-level routing skills such as `ai-first-saas`, `agent-workstream-apps`, `capability-first-backend`, `core-saas-foundation`, and `akka-solution-decomposition` may also need alignment.

## Candidate docs mentioned

- `description-first-application-doctrine.md`
- `internal-app-description-architecture.md`
- `app-description-maintenance-flow.md`
- `app-description-skill-output-contracts.md`
- `app-description-end-to-end-workflow-example.md`
- `intent-driven-usage-flow.md`
- `requirements-to-workstream-development-process.md`
- `domain-workstream-prd-structure.md`
- `prd-to-akka-flow.md`
- `planning-skill-output-contracts.md`
- `solution-plan-to-implementation-queue.md`
- `workstream-contract.md`
- `structured-surface-contracts.md`

## Risks

- Moving active skill directories may break install/reference validation.
- A too-broad migration could disrupt focused implementation skills.
- Over-salvaging legacy content could reintroduce old conceptual debt.

## Unresolved questions

No blocking user questions are currently required to start. The first task should inventory and propose an exact archive/replacement plan before moving active skills or docs.
