# Task AABP-01-002: Implement proposal lifecycle foundation

## Goal

Add backend proposal lifecycle contracts so Save Draft creates a non-active immutable proposal and activation is a separate protected operation.

## Required reads

- `specs/agent-admin-behavior-profile-realization/implementation-map.md`
- `specs/agent-admin-behavior-profile-realization/backlog/01-agent-admin-behavior-profile-build-backlog.md`
- `app-description/domains/core-starter/workstreams/agent-admin/behavior.md`
- `app-description/domains/core-starter/workstreams/agent-admin/realization/api-contracts.md`
- `app-description/domains/core-starter/workstreams/agent-admin/tests/coverage.md`
- `src/main/java/ai/first/application/coreapp/agentadmin/AgentAdminDocAdministrationService.java`
- Relevant Agent Admin service/domain tests.

## Skills

- `akka-agent-behavior-editing`
- `akka-agent-prompt-governance`
- `akka-agent-skill-governance`
- `akka-agent-reference-governance`

## Vertical contract

- Worker: SaaS admin human through `surface_action`; internal editing agent output may create proposal content but cannot mutate active behavior.
- Governed tool/capability: Agent Admin proposal/save/activate tools under `agent-doc-administration`, especially draft/proposal and activation semantics.
- Authorization: SaaS Owner/Admin only; non-admin denied browser-safely.
- Idempotency: Save Draft and Activate require idempotency keys or deterministic duplicate handling.
- Trace: proposal saved, activation allowed/denied, risk classification, authority-expansion flags, and actor/correlation metadata.

## Expected outputs

- Domain/service records or entities for proposals: proposal id, status, base version, risk classification, authority-expansion flags, transcript summary, proposed content/diff, suggested tests, proposed/reviewed/activated actors, correlation/trace refs.
- Service methods for Save Draft, Review/Approve/Reject/Activate, Cancel, stale proposal denial, and direct high-risk activation denial/routing.
- Focused backend tests proving active runtime/current doc remains unchanged after Save Draft and changes only after allowed activation.

## Done criteria

- Direct save-as-active path is replaced or made compatibility-only and not exposed as current product behavior.
- Low-risk activation creates a new current active immutable version with trace/audit facts.
- Medium/high-risk or authority-expanding direct activation is denied or routed with active behavior unchanged.
- Tests cover stale base version/proposal denial and non-SaaS-admin denial where applicable.
- Queue status is updated and changes are committed.

## Required checks

```bash
mvn -Dtest='*AgentAdmin*Service*Test,*AgentAdmin*Doc*Test,*AgentAdmin*Proposal*Test' test
git diff --check
```

## Commit message

`Implement Agent Admin proposal lifecycle foundation`
