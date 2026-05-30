# Task: Decide My Account personal digest worker readiness

## Objective

Decide whether a personal digest worker is justified after deterministic My Account attention and trace foundations exist, and implement only a bounded blocked/readiness path if a real durable worker runtime is not selected.

## Required reads

- AGENTS.md
- specs/full-core-smb-my-account/README.md
- specs/full-core-smb-my-account/conversation-capture.md
- specs/full-core-smb-my-account/my-account-implementation-map.md
- specs/full-core-smb-saas-hardening/agent-worker-opportunities.md
- docs/agent-component-selection-guide.md
- predecessor task changes from TASK-FCSMB-MA-01-002 through TASK-FCSMB-MA-01-004

## In scope

- Evaluate whether personal digest needs durable task identity, progress, notifications, cancellation/failure, retries, or human result review.
- If not justified, record the deferral and ensure any visible digest action returns a typed blocked/readiness `system_message` or workflow-status surface.
- If justified by implemented foundations and existing reusable runtime seams, append a new bounded implementation task before verification instead of implementing a broad worker here.
- Preserve deterministic ownership of attention filtering, trace redaction, authorization, and settings/context updates.

## Out of scope

- Do not implement a successful model-backed worker in this task unless the queue is first updated with a new bounded worker implementation task.
- Do not fabricate deterministic/model-less successful digest output.
- Do not let a worker mutate settings, switch context, open workstreams, grant authority, or bypass redaction.

## Expected outputs

- Updated implementation map or queue notes with the worker readiness decision.
- Optional blocked/readiness surface/action if appropriate.
- New bounded follow-up task(s) only if verification criteria require actual worker implementation.
- Updated queue status.

## Required checks

```bash
rg -n "personal digest|my_account\.personal_digest|AutonomousAgent|blocked_provider_or_runtime|system_message|no deterministic|attention filtering|trace redaction|provider" specs/full-core-smb-my-account templates/ai-first-saas-starter --glob '!**/node_modules/**'
git diff --check
```

Add targeted backend/frontend tests if source changes are made.

## Done criteria

- Personal digest worker state is explicit: deferred/blocked/readiness-only or queued as a new bounded implementation task.
- No model-less successful worker behavior is claimed.
- Task changes and queue update are committed with `full-core-smb: decide my account digest readiness`.
