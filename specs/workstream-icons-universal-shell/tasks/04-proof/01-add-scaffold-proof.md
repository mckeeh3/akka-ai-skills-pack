# TASK-WSI-04-001: Add scaffold proof for v0 left rail icons

## Goal

Add a reproducible proof that a generated/starter v0 app exposes left rail icons for User Admin, Agent Admin, Audit/Trace, and Governance/Policy.

## Required reads

- `specs/workstream-icons-universal-shell/README.md`
- `templates/ai-first-saas-starter/README.md`
- `templates/ai-first-saas-starter/frontend/package.json`
- `bin/scaffold-ai-first-saas-starter.sh`
- `docs/minimum-ai-first-saas-app.md`

## Expected edits

Add either:

1. an automated proof script that scaffolds to a temp directory and runs focused checks, or
2. a documented proof command/report if full scaffold execution cannot run in this environment.

The proof must verify:

- User Admin has a rendered rail icon affordance;
- Agent Admin has a rendered rail icon affordance;
- Audit/Trace has a rendered rail icon affordance;
- Governance/Policy has a rendered rail icon affordance;
- My Account remains accessible from the lower-left signed-in user tile and is not duplicated in the top rail.

Prefer an automated script when possible. If adding a script, keep it deterministic and avoid network dependency after existing project dependencies are installed.

## Required checks

```bash
git diff --check
# Run the new proof command, or document an environment blocker with exact remediation.
rg -n "User Admin|Agent Admin|Audit/Trace|Governance/Policy|My Account|workstream icon|proof" \
  specs/workstream-icons-universal-shell \
  templates/ai-first-saas-starter \
  frontend/src
```

## Done criteria

- A fresh harness can prove the generated/starter v0 app includes left rail icons for the four required core workstreams.
- Pending task is marked done with a completion note and proof result.
- Commit message: `workstream-icons: add v0 rail icon proof`.
