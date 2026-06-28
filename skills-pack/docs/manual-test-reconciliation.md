# Runtime Validation Reconciliation

Runtime validation is a first-class lifecycle phase between implementation verification and further feature work. It is not an informal bug list and it is not permission to patch code before intent is understood. A runtime-validation session exercises the locally runnable app through the intended browser, API, worker, tool, capability, and Akka runtime paths; then it feeds findings back into app-description, specs, pending tasks, runtime-validation evidence, and only then implementation changes.

`Manual runtime testing` remains a compatible legacy phrase for human-operated runtime validation. New task briefs, queues, and specs should prefer `runtime-validation`; use `executionMode: human-manual`, `browser-agent`, `api-agent`, or `scripted-e2e` to say who or what operates the app.

Use this doctrine with the `akka-runtime-feature-verification` and `akka-manual-failure-reconciliation` skills whenever a feature, workstream, or pending-task group is claimed ready for runtime validation or when a human/agent reports runtime failures. See [Runtime validation](runtime-validation.md) for scenario catalogs, setup prerequisites, execution modes, and accumulated validation runs.

## Position in the lifecycle

Runtime validation belongs to the realization loop:

1. Intent exists in app-description/specs and is compiled into implementation tasks.
2. Implementation tasks produce code, tests, traces, and runtime evidence.
3. Runtime verification checks whether the real path reaches at least the claimed readiness level.
4. Runtime validation exercises the user-visible and worker/tool flows locally through documented scenarios.
5. Reconciliation classifies findings and updates intent/spec/task/runtime-validation evidence before more product work continues.

A runtime-validated feature should not be called `runtime-ready` unless the intended local path works at the stated scope with required auth, tenant scope, side effects, trace/audit evidence, provider behavior, and denial/failure behavior. If evidence is limited but sufficient for the stated validation scope, use `manual-ready` or the highest proven lower readiness level.

## Runtime-validation session inputs

Each runtime-validation session starts from an explicit scenario or test charter, not ad hoc clicking. Capture these inputs before testing:

- **Scope:** feature, workstream, surface, worker, governed tool, capability, or pending-task IDs under test.
- **Intent sources:** app-description files, specs, task briefs, acceptance criteria, structured surface contracts, and runtime verification notes.
- **Runtime path hypothesis:** the expected path from browser or non-UI trigger to API/client, actor-adapter, governed tool, backend capability, Akka component/service, view, and trace/audit record.
- **Role and context:** canonical user role, `AuthContext`, tenant/customer/organization, membership, permission set, and denial persona.
- **Provider state:** configured external providers, intentionally missing providers, seed data, demo data, and expected fail-closed behavior.
- **Execution mode:** `human-manual`, `browser-agent`, `api-agent`, or `scripted-e2e`.
- **Evidence target:** readiness level being tested (`manual-ready`, `runtime-ready`, or a lower level), required screenshots/log snippets/commands, and browser-safe trace/status copy.
- **Safety constraints:** no provider secrets in browser payloads, no fixture-only production path, no bypass of auth/tenant/provider boundaries.

## Runtime-validation setup

A validation scenario must say how prerequisites are prepared. Prefer reusable setup docs under `specs/runtime-validation/data-setups/` and, when practical, assisted setup commands that exercise the real running app.

Setup should use the highest practical fidelity:

1. browser/admin UI actions;
2. authenticated API calls;
3. governed admin/setup tools limited to local/dev/test mode;
4. Akka component calls through test harnesses only when the scenario allows a lower readiness claim;
5. direct persistence mutation only for fixture-mode tests.

Record setup mode explicitly: `runtime`, `local-seeded`, `provider-missing-fail-closed`, or `fixture`. Fixture setup is useful for lower-level tests but cannot by itself justify a `runtime-ready` user-visible claim.

## Worker-centric scenario model

For AI-first SaaS workstreams, test scenarios should prove the worker/tool contract instead of only testing screens or individual endpoints. Express each scenario with this chain:

```text
worker -> actor-adapter -> governed tool -> backend capability -> runtime path -> trace/view
```

Use the chain as follows:

- **Worker:** the app-level software worker or workstream role that owns the job, decision, or user-visible promise.
- **Actor-adapter:** the adapter that receives user, agent, scheduled, or system input and invokes the governed operation with the right identity and tenant context.
- **Governed tool:** the permissioned operation exposed to the worker or agent runtime, including policy checks, provider preconditions, idempotency, and safe error reporting.
- **Backend capability:** the application service/capability that performs the business operation without depending on UI fixtures or mock-only state.
- **Runtime path:** the concrete browser/API/Akka path, including component/service calls, side effects, views, traces, audit records, and provider interactions or fail-closed provider errors.
- **Trace/view:** the evidence visible to operators or users without leaking secrets.

A scenario is weak if it only proves a React component, mock response, isolated unit test, or deterministic demo mode when the claim is about a production-like runtime flow.

## Session outputs

Every runtime-validation session produces a reconciliation packet that can be reviewed without rerunning the scenario immediately:

- **Session summary:** date, tester, branch/commit, environment, seed data, providers configured or missing, and commands used to start the app.
- **Scenario results:** pass/fail per worker-centric scenario, with role/context and runtime path actually exercised.
- **Failure inventory:** one row per observed failure using the runtime-validation failure intake table.
- **Evidence:** screenshots, browser console/network notes, API responses, logs, trace IDs, audit/work trace references, and provider/fail-closed messages.
- **Readiness conclusion:** highest justified runtime readiness level and why `runtime-ready` is or is not justified.
- **Reconciliation actions:** app-description/spec updates, pending-task additions or status changes, implementation/test remediation tasks, blocked provider/config prerequisites, and retest requirements.

## Finding categories

Classify every finding before changing code:

| Category | Meaning | First action |
|---|---|---|
| `app-description gap` | Expected behavior is missing, ambiguous, or contradicted by current intent. | Return to interview/change-impact/spec update before implementation. |
| `implementation gap` | Intent exists, but code does not realize it through the intended runtime path. | Create or update focused implementation remediation tasks. |
| `test gap` | Behavior may work, but lacks regression, API, browser, runtime, or manual evidence. | Add test/evidence remediation without claiming higher readiness. |
| `provider/config blocker` | Normal behavior requires WorkOS, Resend, model provider, secrets, external service, or local config that is unavailable. | Require documented fail-closed behavior and name the external prerequisite. |
| `seed/demo-data gap` | Manual path lacks safe local bootstrap data, but production behavior is specified. | Define seed/demo setup without weakening auth, tenant scope, or provider boundaries. |
| `UX/state gap` | Technical behavior works, but copy, loading, stale/conflict handling, accessibility, recovery, or operator status is incomplete. | Create UX/state remediation tied to the same runtime path. |
| `not a bug / expectation change` | Tester expectation conflicts with accepted current intent. | Route to app-description change request if product intent should change. |

Do not merge multiple unrelated failures into one broad task. A remediation task should name the manual failure IDs it fixes and preserve the canonical runtime path.

## Reconciliation workflow

1. **Normalize failures.** Convert notes into the intake table from `akka-manual-failure-reconciliation`.
2. **Check intent first.** Decide whether the expected behavior is already present in app-description/specs. If not, stop implementation and reconcile intent through interview/change-impact/spec update.
3. **Map the runtime path.** Identify the actual and expected browser/API/worker/tool/capability/Akka/trace path.
4. **Classify the gap.** Use exactly one primary category and optional secondary notes.
5. **Update planning artifacts.** Keep blocking failures out of `done`; add remediation tasks with role/context, denial cases, success/failure states, checks, and retest criteria.
6. **Preserve provider fail-closed behavior.** Missing provider configuration may justify a blocked normal flow, but it must still produce actionable, browser-safe errors and trace evidence.
7. **Retest before closure.** A failure-linked task is not `done` until the failing manual scenario is rerun or explicitly blocked by a named external prerequisite.
8. **Re-run runtime verification.** Use `akka-runtime-feature-verification` after remediation to update readiness evidence.

## Intake and evidence templates

Use this failure inventory in runtime-validation run notes and remediation specs:

| ID | Runtime flow | Role/context | Expected behavior | Actual behavior | Intent present? | Runtime path | Classification | Blocking? | Remediation |
|---|---|---|---|---|---|---|---|---|---|
| MF-001 | `<click/action/API>` | `<role/AuthContext/tenant>` | `<expected>` | `<actual>` | `<yes/no/unclear>` | `<worker -> adapter -> tool -> capability -> API/Akka -> trace/view>` | `<category>` | `<yes/no>` | `<task/update>` |

Use this evidence matrix when a runtime-validation run is part of a runtime readiness claim:

| Claim | Intent source | Runtime path | Runtime-validation evidence | Level | Gap |
|---|---|---|---|---|---|
| `<feature/action>` | `<app-description/spec/task>` | `<worker/surface -> adapter/API -> tool/capability -> Akka -> trace/view>` | `<screenshots/logs/API/browser notes>` | `<readiness level>` | `<none or category>` |

## Remediation task minimums

A remediation task created from runtime validation must include:

- manual failure IDs fixed by the task;
- canonical runtime path, including worker, actor-adapter, governed tool, backend capability, Akka component/service, and trace/audit view where applicable;
- role, `AuthContext`, tenant/customer/organization, permission setup, and denial scenario;
- success, validation error, forbidden/hidden/not-found, stale/conflict/idempotency, provider-unconfigured, and recovery expectations where relevant;
- automated checks and manual/browser/API smoke checklist;
- provider configuration status and fail-closed expectations;
- done criteria that forbid closure until the runtime-validation failure is retested or blocked by a named external prerequisite.

## Stop/continue rule

Stop feature expansion when blocking runtime-validation failures remain in the primary path for a claimed feature group. Continue only with focused reconciliation or remediation work until the queue and runtime evidence accurately reflect the highest proven readiness level.
