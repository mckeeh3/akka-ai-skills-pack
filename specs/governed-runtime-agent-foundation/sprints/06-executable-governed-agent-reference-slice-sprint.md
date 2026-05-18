# Sprint 06: Executable governed runtime agent reference slice

## Goal

Materialize the planned minimal governed runtime agent reference slice as executable Java reference code and deterministic tests.

## Why this sprint exists

Sprint 05 made governed runtime agent implementation guidance much clearer, but the main remaining gap is executable coverage. The coverage matrix still marks governed `AgentDefinition`, prompt assembly, governed skills, `readSkill(skillId)`, `ToolPermissionBoundary`, `PromptAssemblyTrace`, `SkillLoadTrace`, and `AgentWorkTrace` patterns as reference-planned or pattern-only.

This sprint turns `minimal-governed-runtime-agent-reference-slice.md` into small reference code and tests without building a full SaaS application.

## Scope

Create a compact, deterministic reference path:

```text
ReferenceAuthContext fixture
→ ReferenceAgentRuntimeResolver
→ active ReferenceAgentDefinition
→ active ReferencePromptVersion
→ compact ReferenceAgentSkillManifest
→ ReferenceToolPermissionBoundary
→ ReferenceSkillReadAuthorizer / readSkill(skillId)
→ optional ManagedReferenceActivityAgent invocation
→ PromptAssemblyTrace + SkillLoadTrace + AgentWorkTrace assertions
```

## Non-goals

- Do not implement WorkOS, billing, invitations, full tenant admin, or frontend UI.
- Do not implement full Event Sourced Entities for governed documents.
- Do not add real provider calls or external side effects.
- Do not replace existing agent examples.
- Do not make reference helper code look like production security infrastructure; it is an executable pattern fixture.

## Acceptance

- Reference records, fixtures, resolver, skill authorizer, and trace sink are executable and tested.
- Optional agent invocation uses deterministic model testing and proves prompt assembly/trace correlation if feasible in a bounded task.
- `docs/agent-coverage-matrix.md` is updated to reflect executable coverage for implemented rows.
- Each task is completed in a fresh harness session and committed independently.

## Related backlog

- `../backlog/06-executable-governed-agent-reference-slice-build-backlog.md`
- `../minimal-governed-runtime-agent-reference-slice.md`
