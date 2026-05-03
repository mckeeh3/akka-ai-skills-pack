# Module-oriented sprint planning

Use this pattern when a large PRD, revised PRD, app-description realization, or broad requirements set is too large for a single solution plan plus flat slice backlog.

Goal: decompose work into **vertical module sprints** that can be implemented and tested full stack, one module at a time.

## When to use

Use module-oriented sprint planning when the input includes any of:

- multiple business modules or bounded capability areas
- backend and browser UI work for the same capability
- enough scope that a flat `specs/backlog/` queue would become hard to navigate
- a need to complete and test one feature area end-to-end before moving to the next

Do not use it for tiny changes where one slice spec and one backlog are enough.

## Repository shape

Preferred shape for large PRD outputs:

```text
specs/
  README.md
  akka-solution-plan.md
  pending-questions.md
  pending-tasks.md
  cross-cutting/
    00-common-domain-and-conventions.md
    01-auth-tenancy-audit.md
    02-ui-style-guide.md
  modules/
    01-identity-access.md
    02-purchase-requests.md
    03-approvals.md
  sprints/
    01-identity-access-sprint.md
    02-purchase-request-core-sprint.md
    03-approval-flow-sprint.md
  backlog/
    README.md
    01-identity-access-build-backlog.md
    02-purchase-request-core-build-backlog.md
    03-approval-flow-build-backlog.md
  tasks/
    README.md
    02-purchase-request-core/
      01-request-entity.md
```

`specs/modules/` captures durable module boundaries.  
`specs/sprints/` captures the ordered vertical delivery plan.  
`specs/backlog/` remains the implementation-ready task source used to materialize `specs/pending-tasks.md`.

For smaller projects, `specs/slices/` is still acceptable. For larger projects, prefer `modules/` + `sprints/`; do not create both `slices/` and `sprints/` unless existing project history already uses slices and you are preserving compatibility.

## Module spec contract

Each `specs/modules/NN-<module>.md` should contain:

- module boundary and purpose
- capabilities owned by the module
- actors and authorization boundaries
- domain objects and state ownership
- backend components likely owned by the module
- frontend screens or navigation areas owned by the module
- integrations and events entering/leaving the module
- cross-cutting specs referenced by the module
- out-of-scope adjacent modules
- related sprints/backlogs

A module spec should be relatively stable across several sprints. It is not the task list.

## Sprint spec contract

Each `specs/sprints/NN-<sprint>.md` should be a full-stack delivery contract:

- sprint goal
- parent module or modules
- dependencies and prerequisite decisions
- backend scope: entities, workflows, views, consumers, timers, endpoints
- frontend scope: screens, forms, navigation, API client calls, realtime behavior
- acceptance behavior: happy paths, validation, no-op/idempotent cases, error cases
- pending questions that block or affect the sprint
- implementation task groups
- module-level full-stack test plan
- done criteria
- explicit defer list

A sprint should end with something demonstrable and testable through the relevant backend and frontend surface.

## Backlog alignment

Each sprint should have one matching backlog file:

```text
specs/sprints/02-purchase-request-core-sprint.md
specs/backlog/02-purchase-request-core-build-backlog.md
```

The backlog should break the sprint into harness-sized tasks. Prefer this order when applicable:

1. shared domain/API records
2. write model entity or workflow
3. views/read models
4. consumers or timed actions
5. HTTP/gRPC/MCP endpoints
6. frontend API client and state
7. frontend screens/forms/realtime behavior
8. component tests
9. endpoint and frontend tests
10. full-stack module smoke or integration test

## Pending questions

Use `specs/pending-questions.md` for decisions that would otherwise force the harness to guess.

Rules:

- associate each question with the exact module/sprint/backlog work it blocks
- block only affected tasks, not the whole project
- allow unblocked foundation or module work to continue
- if browser UI is in scope and no style guide is selected, create a UI style-selection question and block only UI implementation tasks

## Pending tasks

`specs/pending-tasks.md` should group or annotate tasks by sprint/module while preserving stable task IDs.

Acceptable ID patterns:

```text
TASK-001
TASK-002
```

or for large plans:

```text
TASK-02-001
TASK-02-002
TASK-03-001
```

Each task should include required reads for the smallest useful set:

- `specs/akka-solution-plan.md`
- the relevant module spec when module boundaries matter
- the relevant sprint spec
- the matching backlog
- related cross-cutting specs
- a task brief when one exists

## Done criteria for a module sprint

A module sprint is done only when:

- all runnable tasks for the sprint are done or explicitly deferred
- backend component tests pass
- endpoint tests pass for the sprint API surface
- frontend checks/tests pass when UI is in scope
- at least one module-level integration or smoke path verifies the full stack for the sprint
- deferred items and blocked questions are visible in the sprint spec, backlog, or queues

## Anti-patterns

Avoid:

- layer-only sprints such as “all entities” or “all UI” for large PRDs
- module specs that become giant PRD copies
- sprint specs that omit frontend work when the user-facing capability requires it
- pending tasks that jump across unrelated modules in one harness run
- blocking all work because one later module has an unresolved question
- duplicating cross-cutting security/style/audit rules in every sprint

## Related skills and examples

- `examples/purchase-request-module-sprint-plan.md`
- `../skills/akka-prd-to-specs-backlog/SKILL.md`
- `../skills/akka-slice-spec-to-backlog/SKILL.md`
- `../skills/akka-backlog-to-pending-tasks/SKILL.md`
- `pending-task-queue.md`
- `pending-question-queue.md`
