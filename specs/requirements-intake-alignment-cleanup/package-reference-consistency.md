# Package and Reference Consistency Check

## Scope

Checked installable-pack and package-facing references after RIAC cleanup tasks removed or demoted stale intake guidance.

Focused areas:
- `pack/manifest.yaml`
- `pack/README.md`
- `pack/AGENTS.md`
- `pack/EXAMPLES-README.md`
- `install.sh`
- `tools/scaffold-ai-first-saas-starter.sh`
- `templates/ai-first-saas-starter/**` excluding `node_modules`
- active `skills/`, `docs/`, `pack/`, and `templates/` reference searches for removed/stale paths

## Findings

### Removed app-description plan doc

Command:

```bash
rg -n "app-description-skills-plan-backlog|docs/app-description-skills-plan-backlog|app-description-skills-plan" skills docs pack templates install.sh tools package.json package-lock.json --glob '!docs/examples/purchase-request-app-description/**'
```

Outcome: no matches. Active installable-pack docs, skills, templates, and package surfaces no longer reference the removed `docs/app-description-skills-plan-backlog.md` file.

### Legacy purchase-request example references

Command:

```bash
rg -n "purchase-request-prd|purchase-request-app-description|purchase-request-solution-plan|purchase-request-module-sprint-plan|purchase-request-pending-tasks" pack templates install.sh tools package.json package-lock.json
```

Outcome: remaining package-surface hits were limited to `pack/README.md` installed layout documentation. Updated that layout to label purchase-request entries as mechanics-only/conventional references and to include `purchase-request-module-sprint-plan.md`, which is installed because `install.sh` copies `docs/**`.

Command:

```bash
rg -n "purchase-request|app-description-skills-plan-backlog" pack/EXAMPLES-README.md pack/AGENTS.md install.sh tools/scaffold-ai-first-saas-starter.sh templates/ai-first-saas-starter --glob '!**/node_modules/**'
```

Outcome: no matches. Pack-facing installed guidance, examples README, installer, scaffold script, and starter template do not route users through stale purchase-request or removed plan-doc references.

### Manifest reference existence

Command:

```bash
python - <<'PY'
from pathlib import Path
p=Path('pack/manifest.yaml')
refs=[]
inside=False
for line in p.read_text().splitlines():
    if line.startswith('  references:'):
        inside=True; continue
    if inside:
        if line.startswith('  examples:') or line.startswith('  frontendExamples:') or line.startswith('  templates:'):
            break
        s=line.strip()
        if s.startswith('- '): refs.append(s[2:])
missing=[r for r in refs if not Path(r).exists()]
print(f'{len(refs)} references checked')
if missing:
    print('missing:')
    print('\n'.join(missing))
else:
    print('all references exist')
PY
```

Outcome: 21 `pack/manifest.yaml` references checked; all referenced paths exist.

### Manifest stale-reference search

Command:

```bash
rg -n "purchase-request|app-description-skills-plan-backlog" pack/manifest.yaml pack/README.md pack/AGENTS.md pack/EXAMPLES-README.md
```

Outcome: no stale manifest references. Remaining `pack/README.md` purchase-request hits are explicitly mechanics-only/conventional reference labels in the installed layout plus an existing note that legacy purchase-request examples remain mechanics reference material only.

## Updates made

- Updated `pack/README.md` installed layout to mark purchase-request example files as mechanics-only/conventional references.
- Added `purchase-request-module-sprint-plan.md` to the installed layout because the installer copies all `docs/**` and active docs still reference it as a conventional mechanics example.

## Conclusion

No active installable-pack reference points to removed stale content. Remaining package-facing purchase-request references are quarantined as mechanics-only/conventional examples and do not present those artifacts as canonical generated AI-first SaaS target architecture.
