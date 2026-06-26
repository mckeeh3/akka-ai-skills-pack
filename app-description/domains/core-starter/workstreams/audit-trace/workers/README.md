# Workers: Audit/Trace

This directory binds the workers that perform, assist, or deterministically execute Audit/Trace tenant-admin activity-log work. The bindings preserve the current skills-pack chain:

```text
worker → execution harness → actor adapter → governed tool → capability → Akka/API/frontend realization
```

Audit/Trace tenant-admin activity-log scope has three worker classes:

- `tenant-admin-human.md` — the authorized human tenant admin using browser surfaces to search traces, inspect detail, and configure retention.
- `audit-trace-functional-agent-worker.md` — the user-facing workstream assistant / functional-agent worker for navigation and safe explanation only; it has no trace-search, payload-read, export, note, summary, or mutation tool authority in this scope.
- `audit-trace-system-worker.md` — deterministic backend/API/projection/retention participants that record traces, execute authorized searches/detail reads/retention updates, enforce retention expiry, and emit denial/read/update audit evidence.

Surface visibility, prompt text, route availability, and agent output never grant trace authority. Backend capability/governed-tool authorization remains authoritative for every adapter.
