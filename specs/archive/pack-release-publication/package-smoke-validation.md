# Package Smoke Validation

Task: `TASK-PRP-02-001`
Date: 2026-06-02

## Scope validated

- package version consistency
- pack bundle build with starter template resources
- project-level install of the pack
- installed-pack scaffold command execution from packaged resources
- scaffolded starter backend and frontend checks
- focused installed-pack leakage/resource scans

## Temporary paths

Smoke validation used a temporary workspace under `/tmp/tmp.AYHqKcxc5g`:

- bundle output: `/tmp/tmp.AYHqKcxc5g/bundle`
- project install target: `/tmp/tmp.AYHqKcxc5g/install-project`
- scaffold target: `/tmp/tmp.AYHqKcxc5g/scaffolded-app`

## Commands and results

```bash
bash tools/check-version-consistency.sh
```

Result: passed. Version references match manifest version `0.4.0`.

```bash
bash tools/build-pack.sh --output-dir "$BUNDLE_DIR" --github-repo mckeeh3/akka-ai-skills-pack --no-archive
```

Result: passed. Built `akka-ai-skills-pack-0.4.0` and release installer in the temporary bundle directory.

```bash
bash install.sh --location project --project "$PROJECT_DIR" --force
```

Result: passed. Installed project guidance, pack guidance, docs, manifest, skills, Java examples, frontend examples, starter template, and scaffold command.

```bash
"$PROJECT_DIR/.agents/bin/scaffold-ai-first-saas-starter.sh" \
  --target "$SCAFFOLD_DIR" \
  --app-name "Smoke SaaS" \
  --app-slug "smoke-saas" \
  --base-package "com.example.smoke" \
  --maven-group-id "com.example" \
  --yes
```

Result: passed. Scaffold wrote `456` files and generated `specs/scaffold-report.md`.

```bash
cd "$SCAFFOLD_DIR"
mvn test -q
npm --prefix frontend ci
npm --prefix frontend run typecheck
npm --prefix frontend test
```

Result: passed after installing frontend dependencies with `npm ci`.

- backend `mvn test -q`: passed
- frontend `npm ci`: passed, `0` vulnerabilities reported
- frontend `npm --prefix frontend run typecheck`: passed
- frontend `npm --prefix frontend test`: passed, `132` tests passed

## Focused resource presence evidence

Installed pack paths confirmed present:

- `.agents/AGENTS.md`
- `.agents/manifests/akka-ai-skills-pack.yaml`
- `.agents/resources/templates/ai-first-saas-starter/README.md`
- `.agents/resources/templates/ai-first-saas-starter/TEMPLATE-MANIFEST.md`
- `.agents/resources/templates/ai-first-saas-starter/frontend/package.json`
- `.agents/bin/scaffold-ai-first-saas-starter.sh` executable

Scaffolded app paths confirmed present:

- `README.md`
- `pom.xml`
- `frontend/package.json`
- `app-description/README.md`
- `specs/README.md`
- `specs/template/TEMPLATE-MANIFEST.md`
- `specs/template/scaffold-rules.md`
- `specs/scaffold-report.md`

## Leakage scans

Installed-pack scans:

- source-only `specs/` outside `resources/templates/ai-first-saas-starter/specs`: `0`
- `akka-context`, `node_modules`, or `.env.local` paths installed: `0`

The starter template's own `specs/` seed remains intentionally packaged as scaffold resource material.

## Conclusion

Package/install/scaffold smoke validation passes for the current release-publication scope. The installed pack can scaffold the AI-first SaaS starter from packaged resources, and the scaffolded app passes backend tests plus frontend dependency install, typecheck, and contract tests. No package smoke blocker remains.
