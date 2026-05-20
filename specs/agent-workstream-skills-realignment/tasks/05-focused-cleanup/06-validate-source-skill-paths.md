# TASK-AWSR-05-006: Validate and repair source skill path references

## Goal

Find and fix high-impact source skill required-reading path references that are fragile or wrong when resolved from the skill directory.

## Required reads

- `skills/README.md`
- `install.sh` path rewrite logic
- representative skills with source examples/docs references found by audit
- Pi/developer instruction: relative paths in skill files resolve against the skill directory

## Work

1. Create a lightweight path-reference audit script or one-off documented command that checks backtick/list path references in `skills/*/SKILL.md` for source-repo existence when they appear to be local files.
2. Create `specs/agent-workstream-skills-realignment/source-skill-path-reference-audit.md` with:
   - audit command/method;
   - summary counts;
   - high-impact broken references;
   - fixes made;
   - queued follow-up if the issue is broad.
3. Fix the highest-impact broken references in source skills, prioritizing top-level and frequently used implementation skills.
4. Do not edit `.agents/` directly.
5. Update this task entry in `pending-tasks.md` before committing.

## Required checks

- `git diff --check`
- path audit command from the report, or documented limitations if heuristic false positives remain

## Done criteria

- Source skill path issues are documented and the highest-impact breakages are fixed or queued.
- Queue status is updated.
- One git commit is created.

## Suggested commit message

`Audit source skill path references`
