# My Account Full Automated Alignment

## Initiative

Bring the My Account workstream from partially aligned to fully automated-aligned with the current app-description, leaving only explicitly manual/browser/provider-configuration evidence as residual blockers.

## Current intent source

Authoritative current intent lives in:

- `app-description/domains/core-starter/workstreams/my-account/**`
- `app-description/domains/core-starter/capabilities/account-context-and-profile.md`
- `app-description/domains/core-starter/workstreams/ready-to-build-status.md`
- `app-description/domains/core-starter/workstreams/surface-catalog.md`
- `app-description/global/**` for actors, roles, policies, surfaces, tools, and traces

Recent compile evidence updated the command-center/dashboard slice only. This mini-project covers the remaining automated alignment work.

## Problem

The My Account implementation was originally built against older skills-pack and app-description assumptions. The current app-description now requires precise worker/adapter/tool/capability bindings, typed surface contracts, protected runtime paths, tenant/customer-safe denials, durable trace/audit evidence, digest fail-closed/provider behavior, and bounded `human_chat_tool_plan` execution semantics.

A focused command-center compile already reconciled:

- dashboard contract id `my_account.personal_command_center.v1`;
- required `controlPanels[]` payload aliases;
- accessible frontend counter rendering;
- partial lifecycle/source-alignment notes.

The rest of the workstream still needs automated proof and any required code/test repairs.

## Done state

This mini-project is complete when automated checks and source-alignment evidence prove all non-manual My Account alignment items at their stated readiness level:

- source-alignment entries are split by My Account surface/action/runtime slice with evidence and residual status;
- protected backend/API tests cover dashboard open/counter routing, profile/settings saves and denials, context authority, no-access recovery, notification lifecycle, digest fail-closed/read/result, and chat-plan proposal/confirmation/denial/idempotency;
- frontend automated tests/typecheck cover counter-first rendering, profile/settings editable-field submission, context/recovery surfaces, notification triage/lifecycle rendering, digest surfaces, and frontend secret-boundary expectations;
- durable trace/audit/work-trace evidence is tested or explicitly classified as blocked with bounded follow-up;
- provider-backed digest success is either verified through configured test/runtime path or explicitly documented as provider-config blocked while fail-closed behavior is aligned;
- `app-description/domains/core-starter/workstreams/my-account/lifecycle.md` and `realization/source-alignment.md` reflect achieved automated readiness and remaining manual-only checks;
- terminal verification confirms no material automated gaps remain or appends bounded follow-up tasks plus a new terminal verification task.

## Non-goals

- Do not run or claim human manual browser acceptance as complete in this mini-project.
- Do not claim real WorkOS/AuthKit production login smoke unless local config is present and tested separately.
- Do not claim model/provider-backed digest happy path unless concrete provider/test runtime configuration is exercised.
- Do not edit `skills-pack/**`.
- Do not broaden the work into User Admin, Agent Admin, Audit/Trace, or Governance/Policy except where My Account opens/links to those workstreams and must prove safe denial/reauthorization.

## Execution model

- Execute one task per fresh harness context.
- Parent orchestrator should use `pi-subagents` to launch exactly one fresh-context worker subagent at a time.
- Each worker marks exactly one task `in-progress`, performs only that task, runs required checks, marks `done` or `blocked`, commits completed changes plus queue update, and reports the next runnable task.
- If terminal verification finds gaps, append new bounded tasks and a new terminal verification task.

## Required checks by area

Common checks:

```bash
git diff --check
mvn -Dtest='WorkstreamServiceTest,MyAccountBrowserWorkstreamSmokeTest,MyAccountPersonalAttentionDigestServiceTest,MyAccountPersonalAttentionDigestAutonomousAgentTest,AgentBehaviorSeedLoaderTest' test
npm --prefix frontend test -- --run
npm --prefix frontend run typecheck
npm --prefix frontend run build
```

Task briefs may narrow checks when appropriate, but terminal verification must justify any omitted full-area check.
