# Code Context

## Files Retrieved
1. `skills-pack/README.md` (lines 1-18) - declares pack scope, installed assets, and no duplicate baseline model.
2. `skills-pack/AGENTS.md` (lines 15-31) - maintainer constraints for install model and fail-closed runtime doctrine.
3. `skills-pack/skills/README.md` (lines 1-36) - top routing map and core-app-first rules.
4. `skills-pack/install-skills.sh` (lines 1-11, 184-196, 236-242) - proves installer copies all `docs examples templates tools` into `.agents/skills`.
5. `skills-pack/examples/akka-components/README.md` (lines 1-17) - states examples are read-only snapshots, not a second app baseline.
6. `skills-pack/examples/akka-components/src/main/resources/mcp/checkout-guidelines.md` (lines 1-7) - stale shopping-cart/checkout content.
7. `skills-pack/docs/workstream-ui-reference-architecture.md` (lines 1-24) - installed doc points to root-only `specs/` and `frontend/` references.
8. `skills-pack/docs/ai-first-examples-and-tests-gap-list.md` (lines 112-127) - says old static UI fixtures were removed and frontend is root-only.
9. `skills-pack/tools/verify-opinionated-ai-first-saas-pack.sh` (lines 205-247) - guardrails for removed/quarantined content, retired dist output, mandatory security, domain terminology.

## Key Code

Health checks run:

```text
python3 skills-pack/tools/audit-source-skill-paths.py
=> skill_files=156 reference_files=2 checked_refs=641 broken_refs=0

manifest vs source skill dirs
=> manifest skills 156, missing 0, extra 0

./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --dry-run
=> completed; manifest included all skill dirs plus docs/examples/templates/tools/references
```

Main findings:

1. **Definite stale content:** `skills-pack/examples/akka-components/src/main/resources/mcp/checkout-guidelines.md` is a shopping-cart/checkout guide:
   ```text
   # Checkout guidelines
   - Prefer concise summaries of cart state...
   - If the cart is empty...
   - Mention product ids...
   ```
   This is unrelated to the current AI-first SaaS core app / five workstream goals. It is also not listed in `skills-pack/examples/akka-components/README.md` lines 9-13, which only names `application.conf` under resources. `diff -qr src/main/resources skills-pack/examples/akka-components/src/main/resources` showed this `mcp/` directory exists only in the pack example snapshot.

2. **Policy tension / likely overbroad install payload:** `skills-pack/AGENTS.md` lines 15-22 says the installed `.agents/skills` directory is a support library, not a duplicate app baseline. But `install-skills.sh` lines 6 and 184-196 copy all `examples/`, and `examples/akka-components` contains a large source snapshot (`du`: 3.2M; 241 main Java files, 75 test Java files). `examples/akka-components/README.md` lines 3-6 mitigates this by saying read-only and not a second app baseline, but the volume can still confuse harnesses or users.

3. **Overbroad routing/doc surface:** `skills-pack/skills/README.md` is 1,423 lines and acts as both routing map and doctrine. The first 36 lines already mix source repo paths, global-install caveats, core-app-first policy, package rules, and runtime routing. This is likely useful but high-risk for non-contributing repetition and accidental conflicts with focused skill guidance.

4. **Installed docs include root-only references:** `docs/workstream-ui-reference-architecture.md` lines 14-23 points to `specs/workstream-ui-implementation-migration/frontend-stale-code-inventory.md`, `frontend/src/workstream/**`, `frontend/src/main.tsx`, and root frontend tests. It labels one as `source-checkout/root-only`, but the doc is installed under `.agents/skills/docs`; in global installs these paths are target-workspace-dependent and may be dead unless the target is this repo/fork.

5. **Positive alignment:** Top-level pack guidance is broadly aligned with current goals:
   - no duplicate full-pack installer / no duplicate app baseline: `skills-pack/README.md` lines 16-18 and `AGENTS.md` lines 19-22;
   - app source remains in workspace, not `.agents`: `skills/README.md` lines 14-18;
   - fail-closed, real Akka Agent runtime doctrine: `AGENTS.md` lines 26-29;
   - verification guardrails forbid reintroducing quarantined/static UI/dist/optional-security patterns: `tools/verify-opinionated-ai-first-saas-pack.sh` lines 205-247.

## Architecture

`skills-pack/` is a source package for installable harness skills. The installer copies `skills/README.md`, each `skills/*/SKILL.md`, `skills/references`, and whole asset dirs `docs/`, `examples/`, `templates/`, and `tools/` into `.agents/skills`. The root repository remains the canonical runnable app; installed assets are intended as read-only guidance/reference, not app source.

The largest content-health risk is not broken references (none found by the source path audit), but installed guidance breadth: large docs, a huge routing map, and a full Java source snapshot can blur the boundary between harness support library and runnable app baseline. One concrete stale artifact (`checkout-guidelines.md`) should be removed or replaced with a current core-app relevant reference.

## Start Here

Start with `skills-pack/examples/akka-components/src/main/resources/mcp/checkout-guidelines.md` because it is the clearest stale, domain-conflicting artifact and appears to be an unlisted leftover in the installed examples payload.

## Supervisor coordination

No blocker; no supervisor decision requested.
