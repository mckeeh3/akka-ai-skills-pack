# Installed Pack Parity Check

## Task

`TASK-AWSR-05-001`: refresh project-local `.agents/` dogfood output from source and verify representative source-alignment terms are present in the installed pack.

## Commands run

```bash
./install.sh --location project --project . --force
git checkout -- AGENTS.md
rg -n "Agent workstream model" .agents/skills/akka-solution-decomposition/SKILL.md
rg -n "Generated SaaS input contract" .agents/skills/akka-web-ui-api-client/SKILL.md
rg -n "functional/context-area agents|structured surfaces|Do not use capability-first modeling to bypass" .agents/skills/capability-first-backend/SKILL.md
git status --short
git diff --check
```

## Results

| Check | Result | Evidence |
|---|---|---|
| Installer refreshed project `.agents/` | Pass | `./install.sh --location project --project . --force` completed successfully and installed docs, manifest, skills, examples, starter template, and scaffold command. |
| Source maintainer `AGENTS.md` restored | Pass | `git checkout -- AGENTS.md`; subsequent `git status --short` showed only source task/report changes before final queue completion. |
| Installed solution decomposition contains workstream model | Pass | `.agents/skills/akka-solution-decomposition/SKILL.md` contains `Agent workstream model` at lines 96 and 622. |
| Installed web UI API client contains generated SaaS gate | Pass | `.agents/skills/akka-web-ui-api-client/SKILL.md` contains `Generated SaaS input contract` at line 11. |
| Installed capability-first skill preserves workstream-before-capability language | Pass | `.agents/skills/capability-first-backend/SKILL.md` contains the ordered `functional/context-area agents` and `typed structured surfaces and actions` flow, and explicitly says not to bypass functional-agent and surface modeling. |
| `.agents/` remains untracked/ignored | Pass | `.gitignore` ignores `.agents`; `git status --short` did not show tracked `.agents` changes. |

## Mismatches

None found in the required spot checks.

## Conclusion

The installed project-local dogfood pack was refreshed from source and representative realignment guidance is present in installed skills. No `.agents/` content should be committed.
