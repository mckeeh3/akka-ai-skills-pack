# Workers: My Account

This directory binds the workers that perform, assist, or deterministically execute My Account work. The bindings preserve the current skills-pack chain:

```text
worker → execution harness → actor adapter → governed tool → capability → Akka/API/frontend realization
```

My Account has three worker classes:

- `signed-in-member-human.md` — the authenticated human using browser surfaces and, where explicitly confirmed, chat tool plans.
- `my-account-functional-agent-worker.md` — the user-facing workstream assistant / functional-agent worker that explains, proposes, summarizes, and invokes only explicitly allowed read/advisory tools.
- `my-account-system-worker.md` — deterministic backend/API/projection/workflow participants that resolve context, aggregate attention, route openings, and run notification/digest lifecycle plumbing with provenance and audit.

Surface visibility, prompt text, route availability, and agent output never grant authority. Backend capability/governed-tool authorization remains authoritative for every adapter.
