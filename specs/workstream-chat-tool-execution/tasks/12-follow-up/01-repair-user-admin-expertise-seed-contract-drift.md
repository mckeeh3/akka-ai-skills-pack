# TASK-WCTE-12-001: Repair User Admin expertise seed contract drift

## Purpose

Repair the terminal verification blocker where `npm --prefix frontend test -- --run` fails in `frontend/src/workstream-user-admin-expertise.contract.test.mjs` because User Admin seed resources no longer contain the expected denial/authority-expansion wording.

## Required reads

- `AGENTS.md`
- `specs/AGENTS.md`
- `specs/workstream-chat-tool-execution/README.md`
- `specs/workstream-chat-tool-execution/verification-notes.md`
- `frontend/src/workstream-user-admin-expertise.contract.test.mjs`
- `src/main/resources/agent-behavior-seeds/starter-v1/user-admin-agent-expertise.yaml`
- `src/main/resources/agent-behavior-seeds/starter-v1/manifest.properties`
- related User Admin seed/skill/reference files touched by TASK-WCTE-11-001

## Scope

Repair only the User Admin expertise seed/test drift needed to preserve unassigned skill/reference denial, missing `read_skill`/`read_reference` tool-boundary denial, and text-cannot-expand-authority expectations.

Do not broaden chat tool execution behavior or weaken denial expectations to make the test pass.

## Required checks

- `git diff --check`
- `npm --prefix frontend test -- --run`
- `npm --prefix frontend run typecheck`
- targeted seed/import tests if seed resources or checksums change

## Done criteria

- The frontend test `User Admin expertise contract covers unassigned and tool-boundary denials` passes.
- Seed text still does not grant roles, tenant scope, governed-tool access, approval rights, backend capabilities, or chat execution authority.
- Changes and queue update are committed.
