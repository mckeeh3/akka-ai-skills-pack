# MAFA-06-001: Digest/export provider-runtime closure

## Goal

Prove and repair automated My Account personal attention digest/export behavior, including fail-closed provider/runtime semantics and clear classification of provider-backed success readiness.

## Required reads

- `specs/my-account-full-alignment/README.md`
- `app-description/domains/core-starter/workstreams/my-account/surfaces/surfaces.md` digest sections
- `app-description/domains/core-starter/workstreams/my-account/tools/governed-tools.md`
- `src/main/java/ai/first/application/coreapp/myaccount/MyAccountPersonalAttentionDigestService.java`
- `src/main/java/ai/first/application/coreapp/myaccount/**Digest**`
- existing digest/autonomous-agent tests

## Skills

- `akka-autonomous-agents`
- `akka-autonomous-agent-testing`
- `akka-runtime-feature-verification`
- `ai-first-saas-audit-trace`

## Implementation requirements

- Test start/read/cancel/result/accept/reject flows and task ownership checks.
- Test missing provider/runtime/governed-tool configuration fails closed with browser-safe blocker and no fake/model-less normal-runtime success.
- Test completed advisory result review does not mutate source attention.
- Test lifecycle events/progress/result/blocked surfaces and trace refs.
- Verify idempotency for start/replay and safe repeated review actions.
- Determine whether provider-backed happy path is verifiable locally. If not, update alignment/readiness to say provider-backed success remains config-blocked while fail-closed behavior is aligned.
- Repair runtime/test drift discovered by these checks.

## Vertical workstream contract

- Lifecycle / readiness target: build-compile to backend-ready for fail-closed and task lifecycle; provider-backed runtime-ready only if configured and tested.
- Workstream / functional agent: My Account / `my-account-agent`; system worker/autonomous digest worker.
- Surface graph/action edge: digest progress/result/blocked surfaces and dashboard digest panel.
- Governed-tool id and exposure: `request-personal-digest-export`; actor adapters `surface_action`, `agent_tool_call` read/proposal where granted, API/internal/workflow-like service path.
- Confirmation / approval / transaction: digest start/cancel/review idempotency; advisory-only result; no source attention mutation.
- Capability id: `my_account.personal_attention_digest.*` capability ids.
- AuthContext / tenant scope: current account, selected tenant/customer, task ownership, hidden evidence omission.
- Akka substrate: autonomous-agent runtime adapter/service tests.
- Audit/work trace requirements: lifecycle/progress/result/blocked trace evidence.
- Local validation path: targeted Maven tests and source-alignment update.

## Required checks

```bash
mvn -Dtest='MyAccountPersonalAttentionDigestServiceTest,MyAccountPersonalAttentionDigestAutonomousAgentTest,DigestExportServiceTest,WorkstreamServiceTest' test
git diff --check
```

## Done criteria

- Digest automated behavior is aligned or provider-backed success is explicitly config-blocked.
- Fail-closed behavior and no-fake-success semantics are tested.
- Source-alignment digest entry is updated.
- Queue status is updated and changes are committed.
