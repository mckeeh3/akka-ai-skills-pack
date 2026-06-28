# MAFA-03-001: Durable trace and audit verification

## Goal

Verify and repair durable trace/audit coverage for consequential My Account reads/actions and chat-plan lifecycle events. Returned synthetic trace refs alone are not enough for slices that current intent says must emit durable trace facts.

## Required reads

- `specs/my-account-full-alignment/README.md`
- `app-description/domains/core-starter/workstreams/my-account/traces/work-traces.md`
- `app-description/global/traces/foundation-trace-patterns.md`
- `src/main/java/ai/first/application/foundation/audit/**`
- `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`
- `src/main/java/ai/first/application/coreapp/myaccount/**`
- trace/audit related backend tests

## Skills

- `akka-agent-work-trace`
- `ai-first-saas-audit-trace`
- `akka-runtime-feature-verification`

## Implementation requirements

- Identify the concrete durable audit/work trace sinks used by My Account.
- Add or repair tests for trace facts on dashboard/profile/settings/context/notification/digest/chat-plan consequential actions where current intent requires durable facts.
- Verify fields include actor/account, selected context, capability/tool/action id, adapter/source, result state, redaction level, correlation/idempotency when applicable.
- If a durable sink is missing for a required slice, implement the smallest runtime repair or block with a precise follow-up task.
- Update source-alignment evidence.

## Vertical workstream contract

- Lifecycle / readiness target: build-compile trace alignment; backend-ready for automated trace checks.
- Workstream / functional agent: My Account / `my-account-agent`.
- Governed-tool id and exposure: all My Account consequential tools across `surface_action`, `human_chat_tool_plan`, API/internal.
- Capability id: `account-context-and-profile` plus notification/digest capability ids.
- AuthContext / tenant scope: selected `AuthContext`, tenant/customer/account scope in trace evidence.
- Akka substrate: service/audit repository tests; no UI required.
- Audit/work trace requirements: central purpose of task; durable fact verification required.
- Local validation path: targeted Maven tests and `git diff --check`.

## Required checks

```bash
mvn -Dtest='*Trace*Test,WorkstreamServiceTest,MyAccountPersonalAttentionDigestServiceTest' test
git diff --check
```

Use an updated targeted equivalent if test names differ.

## Done criteria

- Durable trace/audit evidence is proven for all automated My Account slices or gaps are queued as bounded follow-ups.
- Source-alignment trace/audit entry is updated.
- Queue status is updated and changes are committed.
