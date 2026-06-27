# Task AABP-02-001: Align runtime loader and traces

## Goal

Make runtime agent behavior resolve only from active behavior-profile/docs and expose Agent Admin-visible trace metadata for profile/doc/tool/model decisions.

## Required reads

- `specs/agent-admin-behavior-profile-realization/implementation-map.md`
- `specs/agent-admin-behavior-profile-realization/tasks/01-backend/03-behavior-profile-version-assignments.md`
- `app-description/domains/core-starter/workstreams/agent-admin/behavior.md`
- `app-description/domains/core-starter/workstreams/agent-admin/traces/work-traces.md`
- `app-description/domains/core-starter/workstreams/agent-admin/tests/coverage.md`
- Runtime loader/tool resolver/trace sink classes and tests.

## Skills

- `akka-agent-component-tools`
- `akka-agent-tool-boundaries`
- `akka-agent-work-trace`
- `akka-agent-testing`

## Vertical contract

- Worker: runtime resolver/loader system worker and managed agents.
- Actor adapters: `internal_call` for runtime profile/doc resolution, `agent_tool_call` for `readSkill` and `readReferenceDoc`.
- Governed tools: `readSkill(skillId)`, `readReferenceDoc(referenceId)`, generated tool decisions through resolved profile/tool boundary.
- Runtime behavior: tenant active profile fallback to global active profile; inactive/proposed drafts not loaded.
- Trace: profile resolution, prompt assembly, model-policy decision, assigned skill/reference load, generated-tool assignment decision, tool-boundary denial, AgentWorkTrace metadata.

## Expected outputs

- Runtime loading uses activated behavior profile/docs only.
- `readSkill` / `readReferenceDoc` deny unassigned/inactive/cross-scope documents safely.
- Generated tool calls are constrained by resolved assigned generated tool list and backend tool boundary.
- Trace surface/service data includes safe metadata required by app-description and excludes full skill/reference body content.
- Tests cover active-vs-proposed draft, tenant fallback, assigned/unassigned reads, generated-tool denial, model/provider fail-closed, and trace visibility.

## Done criteria

- No normal runtime path loads draft/proposed behavior as active behavior.
- Missing provider/model config fails closed with actionable browser-safe metadata and trace refs.
- Queue status is updated and changes are committed.

## Required checks

```bash
mvn -Dtest='*AgentRuntimeToolResolver*Test,*AgentRuntimeTrace*Test,*AgentRuntimeService*Test,*WorkstreamRuntimeAgent*Test,*AgentAdmin*Test' test
git diff --check
```

## Commit message

`Align Agent Admin runtime loader traces`
