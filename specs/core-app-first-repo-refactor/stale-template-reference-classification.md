# Stale Template Reference Classification

## Purpose

Classify remaining matches for the dissolved generated-app source copy after the core-app-first refactor. The repository root is the canonical runnable core app; `skills-pack/` owns installable pack assets and focused examples.

## Search scope

Command used for this pass:

```bash
rg -l --hidden --glob '!target/**' --glob '!frontend/node_modules/**' --glob '!node_modules/**' --glob '!skills-pack/dist/**' --glob '!specs/core-app-first-repo-refactor/**' --glob '!specs/archive/**' --glob '!.git/**' 'templates/ai-first-saas-starter|scaffold-ai-first-saas-starter|resources/templates/ai-first-saas-starter|starter template|full-app template|scaffold-first' specs docs README.md AGENTS.md skills-pack
```

The search still returns historical/provenance matches across completed specs. A focused queue-block scan after this task's edits found **zero pending or in-progress task blocks** that still contain those stale patterns.

## Active runnable references repaired now

- `specs/workstream-visual-sessions/pending-tasks.md`
- `specs/workstream-visual-sessions/tasks/04-phase-1-1-remediation/04-reverify-runtime-ux-with-manual-steps.md`

`TASK-WVS-04-004` was the only currently pending task block found in a matching queue. It previously required reads/checks against the removed generated-app source copy. It now verifies only the canonical root `frontend/**` path and records the core-app-first compatibility note without directing a fresh context to read or test removed paths.

## Remaining matches classified as historical/provenance

The remaining matches are in completed task notes, completed pending-task entries, past verification reports, migration inventories, and legacy mini-projects that describe the earlier source-copy/template model. They are not currently runnable queue instructions because their matching task blocks are already `done` or are narrative records.

Examples of this provenance category include:

- completed core app/runtime specs such as `specs/core-app-feature-completion/**`, `specs/core-app-full-stack-readiness/**`, `specs/full-core-*`, and workstream completion queues;
- earlier migration and release specs such as `specs/minimum-ai-first-app-migration/**`, `specs/requirements-to-workstream-process-migration/**`, `specs/agent-workstream-*migration/**`, `specs/pack-release-publication/**`, and `specs/ai-first-saas-starter-release-readiness/**`;
- historical starter-app-template planning under `specs/ai-first-saas-starter-app-template/**`, which is now superseded by this core-app-first refactor rather than a current implementation source;
- completed visual-session sync tasks under `specs/workstream-visual-sessions/**` that record prior source-copy sync work but no longer provide pending runnable instructions.

These files may retain old path text as provenance unless a future task explicitly reopens that spec or the terminal verification finds a specific current guidance surface still sending users to removed paths.

## Obsolete completed queues/specs

Queues with matching `pending-tasks.md` files but no pending work are treated as completed historical records. This pass did not rewrite their completed entries because the queue contract says to preserve task IDs and because broad edits to completed notes would risk obscuring implementation history.

The old `specs/ai-first-saas-starter-app-template/**` mini-project is classified as superseded planning history for the dissolved duplicate generated-app source copy. If it is ever reopened, it should first receive a dedicated supersession/archive task rather than resuming old template-build instructions.

## Unrelated phrase use

Some matches use phrases such as "starter template" or "full-app template" to describe past context, validation gaps, or release-history vocabulary. Those are acceptable when they do not instruct current users or fresh harness contexts to read, edit, validate, package, or scaffold removed `templates/ai-first-saas-starter/**` paths.

## Terminal verification guidance

For the next terminal verification, treat a remaining match as material only if it appears in one of these places:

1. a pending or in-progress task block;
2. current root app or skills-pack user guidance that instructs users to scaffold or read the removed generated-app source copy;
3. a validation, packaging, or install script path that requires the removed generated-app source copy at runtime.

Do not treat completed queue notes or historical mini-project prose as blockers unless they are being used as current runnable guidance.
