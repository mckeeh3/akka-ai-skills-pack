# TASK-ADIA-02-003: Align Agent Admin implementation evidence

## Summary

Reconcile Agent Admin lifecycle/source-alignment against real backend/frontend/tests/runtime-validation evidence, and queue exact remediation where gaps remain.

## Required reads

- `specs/app-description-implementation-alignment/workstreams/agent-admin-alignment-plan.md`
- `specs/app-description-implementation-alignment/source-evidence-inventory.md`
- `app-description/domains/core-starter/workstreams/agent-admin/**`
- relevant source/frontend/test files identified by the inventory

## Skills

- `app-description-change-impact`
- `akka-agents`
- `akka-agent-behavior-profiles`
- `akka-agent-prompt-governance`
- `akka-agent-skill-governance`
- `akka-agent-reference-governance`
- `akka-agent-tool-boundaries`
- `akka-runtime-feature-verification`

## Expected outputs

- Updated Agent Admin `lifecycle.md` and/or `realization/source-alignment.md` evidence.
- Exact follow-up entries in `implementation-follow-up-queue.md` for remaining gaps.

## Required checks

- `git diff --check`
- Targeted proof commands for mapped source/test/frontend files.

## Done criteria

- Agent Admin is marked aligned, partially aligned, blocked, or still stale with evidence.
- No runtime-ready claim is made without real runtime evidence.
- Changes and queue update are committed.

## Vertical workstream contract

Agent Admin functional-agent workstream; attention category behavior-change proposal/provider-config/loader-denial; role-specific dashboard / surface Agent Admin catalog/detail/governance/test-console/proposal; surface graph node/action edge AgentDefinition/PromptDocument/SkillDocument/ReferenceDocument/manifest/tool-boundary/model/test-console actions and results; governed-tool id/type/exposure managed-agent governance tools; actor adapter/source `surface_action`, `human_chat_tool_plan`, `agent_tool_call`, API/internal runtime loader; confirmation/approval behavior authority-expansion approval and chat confirmation; idempotency/transaction/result behavior draft/proposal/version activation/result/partial-failure surfaces; capability or foundation scope managed-agent governance; AuthContext / roles / tenant scope SaaS admin tenant scope and provider secret boundary; API / frontend / realtime path Agent Admin route/API/runtime-loader mappings; audit/work trace expectation PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace, AgentWorkTrace, provider fail-closed and loader denial traces; validation path `git diff --check` plus mapped evidence proof.
