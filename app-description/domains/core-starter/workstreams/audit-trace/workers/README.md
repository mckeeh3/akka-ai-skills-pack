# Workers: Audit/Trace

This directory binds the workers that perform, assist, or deterministically execute Audit/Trace investigation work. The bindings preserve the current skills-pack chain:

```text
worker → execution harness → actor adapter → governed tool → capability → Akka/API/frontend realization
```

Audit/Trace has four worker classes:

- `tenant-admin-human.md` — authorized tenant/Organization admin using browser surfaces and confirmed read-only chat plans to search, inspect, correlate, summarize, review support-access evidence, and request redacted exports for their selected tenant.
- `saas-support-human.md` — SaaS owner/support operator using Audit/Trace only under platform support authority or active support-access scope; access is redacted, time-bounded, reviewable, and traced.
- `audit-trace-functional-agent-worker.md` — the user-facing Audit Trace functional agent that can assist read-only investigations through confirmed `human_chat_tool_plan` and bounded `agent_tool_call` adapters, without widening scope or approving support/export decisions.
- `audit-trace-system-worker.md` — deterministic backend/API/projection/consumer/runtime-validation participants that record traces, execute authorized search/detail/correlation/export workflows, enforce redaction/support-access/retention policy, detect trace gaps, and emit evidence.

Surface visibility, prompt text, route availability, support status, and agent output never grant trace authority. Backend capability/governed-tool authorization remains authoritative for every adapter.
