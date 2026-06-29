# Runtime Validation

Runtime validation is the app-facing integration test layer that proves a generated SaaS or SaaS Foundation App works through the intended local running application path. It replaces vague ad hoc app-checking language with explicit validation scenarios that can be executed by a human tester, a browser-capable agent, an API agent, or a scripted end-to-end runner.

Runtime validation is the three-phase lifecycle's final phase. Human-operated validation is represented with `executionMode: human-manual`; new tasks and specs should use `runtime-validation` vocabulary.

## Definition

A runtime-validation scenario exercises the running app through the same operational path a worker would use:

```text
worker / tester / browser agent
  -> frontend surface or non-UI trigger
    -> API/client/actor adapter
      -> governed tool
        -> backend capability
          -> Akka component/service/substrate
            -> view/projection/side effect
              -> audit/work trace/result surface
```

This is a form of operational integration testing. It is stronger than unit, component, fixture, or frontend-only checks because it validates the joins between surfaces, APIs, authorization, Akka persistence, projections, provider/fail-closed behavior, and trace evidence.

## Execution modes

Use one of these execution modes on runtime-validation tasks or run records:

- `human-manual` — a human operates the running app and records evidence.
- `browser-agent` — an agent operates the running browser/app directly and records evidence.
- `api-agent` — an agent exercises protected APIs and validates resulting state/traces.
- `scripted-e2e` — a repeatable script such as Playwright or an app-specific runner performs the scenario.

The mode changes who performs the validation, not the required runtime path or evidence.

## Runtime-validation corpus

As the app grows, accumulate a durable validation corpus in the target project. Recommended structure:

```text
specs/runtime-validation/
  README.md
  environments/
    local-dev.md
    local-dev-with-providers.md
  personas/
    org-admin.md
    member.md
    support-operator.md
  data-setups/
    base-tenant.md
    invited-user.md
    customer-with-domain-state.md
  scenarios/
    auth/
      RV-AUTH-001-login-and-me.md
    admin/
      RV-ADMIN-001-invite-user.md
    <domain>/
      RV-<DOMAIN>-001-<scenario>.md
  runs/
    2026-06-28-RUN-0001.md
```

- `scenarios/` are stable validation definitions the app should continue to satisfy.
- `environments/`, `personas/`, and `data-setups/` define reusable prerequisites.
- `runs/` record actual executions, evidence, failures, blockers, and readiness conclusions.

A project may start with a small `specs/runtime-validation.md` file, but directory form is preferred once scenarios need reusable setup or run history.

## Scenario minimum contract

Each scenario should include YAML frontmatter or an equivalent header:

```yaml
id: RV-<AREA>-001
title: <worker-visible behavior>
workstream: <workstream-id or foundation/cross-cutting>
surface: <surface-id or non-ui>
persona: <persona-id>
environment: local-dev
dataSetup:
  - base-tenant
executionMode: human-manual
readinessClaim: runtime-ready
```

And these sections:

```md
# Purpose

What business/workstream promise this validates.

# Prerequisites

- App start command and URL.
- Required provider/config state.
- Required persona/user/role/AuthContext.
- Required tenant/customer/org/domain data.
- Referenced setup docs or setup command.

# Runtime path

worker -> surface/trigger -> API/client/adapter -> governed tool -> capability -> Akka substrate -> trace/view/result

# Setup

How to prepare state. Prefer real app capabilities over fixture mutation.

# Steps

Numbered user/agent/API actions.

# Expected results

Observable success, denial, validation, idempotency, provider-unconfigured, trace/audit, and recovery outcomes.

# Evidence to capture

URLs, visible state, network/API status, trace/audit ids, logs, screenshots or DOM observations, and provider/fail-closed messages.

# Failure classification hints

Likely categories such as implementation gap, app-description gap, provider/config blocker, seed-data gap, test gap, UX/state gap, or expectation change.
```

## Setup levels

Runtime validation requires reproducible prerequisites. Use the highest practical setup level:

1. **Documented setup** — the scenario explains the human-operated or API steps needed to create tenant, users, roles, domain records, provider state, and route entry.
2. **Assisted setup** — a helper command prepares most prerequisites and prints remaining instructions.
3. **Fully automated setup** — a runner prepares state, executes the scenario, and captures evidence.

Prefer setup through real application pathways:

1. browser/admin UI actions;
2. authenticated HTTP/API calls;
3. governed admin/setup tools available only in local/dev/test mode;
4. Akka component calls through test harnesses when the scenario explicitly permits a lower readiness claim;
5. direct persistence mutation only for narrow fixture-mode tests, never as proof of a production-like runtime path.

Setup docs and commands must state their mode:

```yaml
setupMode: runtime | local-seeded | provider-missing-fail-closed | fixture
```

Only `runtime`, `local-seeded`, or explicit `provider-missing-fail-closed` setup can support a `runtime-ready` claim for user-visible behavior. `fixture` setup may support lower-level evidence but must not be presented as full runtime readiness.

## Runtime-validation task

A task may execute one or more catalog scenarios. It should reference scenarios instead of redefining them:

```yaml
id: TASK-000142
type: runtime-validation
status: pending
phase: runtime-validation
scenarios:
  - RV-ADMIN-001
executionMode: browser-agent
environment: local-dev
readinessTarget: runtime-ready
```

A runtime-validation task must record:

- scenario pass/fail/block status;
- actual environment, commit, branch, start commands, and provider/config state;
- evidence captured;
- readiness conclusion;
- remediation tasks or blocking questions created.

## Outcome rules

- If all required scenarios pass with evidence, the validated scope may advance to the recorded readiness level.
- If the app cannot be started, required auth/users/providers are unavailable, or setup cannot safely create prerequisites, move the validation task to blocked with exact unblock conditions.
- If validation finds defects, create focused remediation tasks linked to scenario/failure ids and keep the feature below `runtime-ready` until retested.
- If a scenario exposes missing or ambiguous intent, route to app-description/spec reconciliation before implementation.

## Relationship to implementation tasks

A feature-bearing implementation task must either:

- include enough runtime-validation evidence for its claimed readiness level; or
- create/update a runtime-validation scenario and queue a runtime-validation task; or
- explicitly state why no runtime validation applies, such as docs-only, planning-only, or internal non-runtime work.

Do not claim a user-visible feature is `runtime-ready` from unit tests, component tests, typechecks, or fixture-only setup alone.
