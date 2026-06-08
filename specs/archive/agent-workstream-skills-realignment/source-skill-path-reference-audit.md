# Source Skill Path Reference Audit

## Scope

Task: `TASK-AWSR-05-006`.

Audited `skills/*/SKILL.md` references that look like source-repository paths and should resolve from the skill directory, per harness/developer instruction.

## Audit command

```bash
python3 tools/audit-source-skill-paths.py
```

Current result:

```text
skill_files=150 checked_refs=1203 broken_refs=0
```

Additional targeted check for the high-impact stale pattern:

```bash
rg -n "\.\./\.\./\.\./(src|docs|frontend|pom\.xml)" skills || true
```

Current result: no matches.

## Method

Created `tools/audit-source-skill-paths.py` as a lightweight source hygiene checker. It:

- scans backtick references in `skills/*/SKILL.md`;
- treats source-pack references to `../../docs`, `../../src`, `../../frontend`, top-level files, and repo-root paths as candidates;
- resolves relative candidates from each skill directory;
- reports candidate references whose targets do not exist;
- intentionally ignores target-project placeholders such as `specs/*`, `app-description/*`, `frontend/*`, and frontend-build output references that are meant to be read in a generated application workspace rather than in this source repository.

## Findings

Initial audit found widespread high-impact broken source references using `../../../src/...`, `../../../docs/...`, and related forms in source skills. From a source skill directory such as `skills/akka-agents/`, those paths resolve outside the repository; the correct source-repo paths are `../../src/...`, `../../docs/...`, `../../frontend/...`, and `../../pom.xml`.

The issue mostly affected high-use focused implementation skills for:

- agents;
- HTTP, gRPC, and MCP endpoints;
- event sourced entities and key value entities;
- workflows, views, consumers, and timed actions;
- source examples and component tests referenced from orchestrator skills.

## Fixes made

Normalized source skill references across affected `skills/*/SKILL.md` files:

- `../../../src/` -> `../../src/`
- `../../../docs/` -> `../../docs/`
- `../../../frontend/` -> `../../frontend/`
- `../../../pom.xml` -> `../../pom.xml`

This preserves installer behavior: `install.sh` already rewrites installed skill references for `../../src/` and `../../frontend/`, while `../../docs/` is the correct installed-pack relative path from `.agents/skills/<skill>/` to `.agents/docs/`.

## Limitations

The audit is heuristic. It does not attempt to validate every text token that resembles a path because many skill files intentionally include target-project examples such as `specs/pending-tasks.md`, `frontend/package.json`, or `app-description/55-ui/style-guide.md`. Those are not source-pack file references and should not be resolved against the skill source directory.

## Follow-up

No immediate follow-up task is required for the high-impact broken source path pattern. Future path hygiene work can extend the script if the repository adds a more formal annotation for target-project paths versus source-pack paths.
