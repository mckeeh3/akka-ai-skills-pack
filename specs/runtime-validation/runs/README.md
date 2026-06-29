# Runtime-validation run records

Run records will be added here by later execution tasks. This directory intentionally contains no pass/fail evidence yet.

Each run record should include:

- commit, branch, environment, start/reset command, and provider configuration state;
- seed/setup command output and setup evidence;
- scenarios executed and execution mode;
- persona/AuthContext/tenant/customer scope used;
- UI/API/runtime observations and captured evidence;
- audit/work trace ids and provider/fail-closed evidence;
- scenario result: `passed`, `failed`, or `blocked`;
- readiness conclusion and linked remediation tasks.

Scenario definitions alone must not be treated as runtime-ready evidence.
