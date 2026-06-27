# Task AABP-05-002: Repair full-suite seed-count blocker from terminal verification

## Goal

Unblock the Agent Admin terminal verification by reconciling the full-suite `AgentBehaviorSeedLoaderTest` seed-count failure found by `AABP-05-001`.

## Required reads

- `specs/agent-admin-behavior-profile-realization/verification-notes.md`
- `specs/agent-admin-behavior-profile-realization/pending-tasks.md`
- `src/test/java/ai/first/application/foundation/agent/AgentBehaviorSeedLoaderTest.java`
- Source files directly needed to explain or repair the seed-count delta.

## Scope

- Determine whether the observed import count delta (`expected: <49> but was: <54>`) is stale test evidence from prior Agent Admin behavior-profile seed additions or an actual seed/accounting defect.
- Make the smallest safe repair.
- Do not broaden Agent Admin product behavior beyond the current app-description.

## Expected outputs

- Repaired seed-count/accounting evidence, or a narrower blocker note if the delta represents a product defect that cannot be safely fixed in this task.
- Updated `pending-tasks.md` status and notes.

## Required checks

```bash
mvn -Dtest=AgentBehaviorSeedLoaderTest test
mvn test
git diff --check
```

## Done criteria

- Full backend suite no longer fails on stale seed-count assertions, or the underlying seed-accounting defect is isolated with a bounded blocker.
- Changes are committed.

## Commit message

`Repair Agent Admin terminal seed count blocker`
