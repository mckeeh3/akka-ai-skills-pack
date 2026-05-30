# Validation Results: Release Readiness After Five-Core v0

## TASK-REL-01-001: Starter and pack validation

Date: 2026-05-29

### Summary

- `tools/validate-ai-first-saas-starter-fullstack.sh`: passed.
- `bash tools/check-version-consistency.sh`: passed.
- `bash tools/build-pack.sh --clean --no-archive`: failed first because the starter template frontend source was out of sync with `frontend/src`; passed after syncing `templates/ai-first-saas-starter/frontend/src` from `frontend/src`.
- `git diff --check`: passed.
- Generated `dist/` artifacts were produced by the pack build but are intentionally not included in the task commit.

### Evidence

#### Fullstack starter validation

Command:

```bash
tools/validate-ai-first-saas-starter-fullstack.sh
```

Result: passed.

Evidence:

- Scaffolded a disposable target under `/tmp/ai-first-saas-starter-fullstack.fTnhBu`.
- Verified rendered backend, frontend, planning paths, and governed runtime tool registration markers.
- Scaffolded backend `mvn test` succeeded: `Tests run: 129, Failures: 0, Errors: 0, Skipped: 1`.
- Frontend `npm install` completed with `found 0 vulnerabilities`.
- Frontend contract tests passed: `tests 120`, `pass 120`, `fail 0`, `skipped 0`.
- Frontend `npm run typecheck` passed.
- Frontend `npm run build` passed and produced Akka static resources.
- Built static assets scan found no backend secret markers.
- Optional real model provider smoke ran and passed through backend workstream message submission without provider-secret leaks in smoke logs, frontend env, or static assets.

#### Version consistency

Command:

```bash
bash tools/check-version-consistency.sh
```

Result: passed.

Evidence:

```text
[check-version] README.md and pack/README.md version references match manifest version 0.2.12
```

#### Pack build

Initial command:

```bash
bash tools/build-pack.sh --clean --no-archive
```

Initial result: failed.

Evidence:

```text
[build-pack][error] Starter template frontend/src is out of sync with frontend/src. Sync templates/ai-first-saas-starter/frontend before building a release.
```

Release-blocking fix applied in this task:

```bash
rm -rf templates/ai-first-saas-starter/frontend/src
cp -R frontend/src templates/ai-first-saas-starter/frontend/src
diff -qr --exclude node_modules --exclude .env.local frontend/src templates/ai-first-saas-starter/frontend/src
```

The sync proof produced no diff output.

Retry command:

```bash
bash tools/build-pack.sh --clean --no-archive
```

Retry result: passed.

Evidence:

```text
[build-pack] Cleaning previous outputs for akka-ai-skills-pack-0.2.12
[build-pack] Building akka-ai-skills-pack-0.2.12
[build-pack] GitHub repo: mckeeh3/akka-ai-skills-pack
[build-pack] Bundle directory:  /home/hxmc/ai/akka-ai-skills-pack/dist/akka-ai-skills-pack-0.2.12
[build-pack] Release installer: /home/hxmc/ai/akka-ai-skills-pack/dist/install-akka-ai-skills-pack-0.2.12.sh
[build-pack] Done
```

#### Diff whitespace check

Command:

```bash
git diff --check
```

Result: passed with no output.

## TASK-REL-01-002: Source install and scaffold validation

Date: 2026-05-29

### Summary

- Source install into a disposable project target: passed.
- Installed scaffold dry-run against the installed target: passed.
- Installed scaffold non-dry-run into a separate disposable empty target: passed.
- Rendered scaffold smoke paths `specs/scaffold-report.md`, `pom.xml`, `src/`, and `frontend/`: present.
- No disposable target artifacts were created inside the repository.

### Evidence

Disposable targets:

```text
install target: /tmp/akka-pack-install-smoke.KjEaTt
scaffold target: /tmp/akka-pack-scaffold-smoke.sIvxtL
```

#### Source install

Command:

```bash
bash install.sh --location project --project /tmp/akka-pack-install-smoke.KjEaTt
```

Result: passed.

Evidence:

```text
[install] Install complete
[install] Installed pack guidance:    /tmp/akka-pack-install-smoke.KjEaTt/.agents/AGENTS.md
[install] Installed docs:             /tmp/akka-pack-install-smoke.KjEaTt/.agents/docs
[install] Installed manifest: /tmp/akka-pack-install-smoke.KjEaTt/.agents/manifests/akka-ai-skills-pack.yaml
[install] Installed skills:   /tmp/akka-pack-install-smoke.KjEaTt/.agents/skills
[install] Installed starter template:    /tmp/akka-pack-install-smoke.KjEaTt/.agents/resources/templates/ai-first-saas-starter
[install] Installed scaffold command:    /tmp/akka-pack-install-smoke.KjEaTt/.agents/bin/scaffold-ai-first-saas-starter.sh
```

#### Installed scaffold dry-run

Command:

```bash
/tmp/akka-pack-install-smoke.KjEaTt/.agents/bin/scaffold-ai-first-saas-starter.sh \
  --target /tmp/akka-pack-install-smoke.KjEaTt \
  --app-name "Release Smoke" \
  --base-package ai.first \
  --dry-run
```

Result: passed.

Evidence:

```text
[scaffold] Template: /tmp/akka-pack-install-smoke.KjEaTt/.agents/resources/templates/ai-first-saas-starter
[scaffold] Target:   /tmp/akka-pack-install-smoke.KjEaTt
[scaffold] App:      Release Smoke (ai-first-saas-starter)
[scaffold] Package:  ai.first
[scaffold] Files:    291
[dry-run] write specs/scaffold-report.md
```

#### Installed scaffold non-dry-run

Command:

```bash
/tmp/akka-pack-install-smoke.KjEaTt/.agents/bin/scaffold-ai-first-saas-starter.sh \
  --target /tmp/akka-pack-scaffold-smoke.sIvxtL \
  --app-name "Release Smoke" \
  --base-package ai.first
```

Result: passed.

Evidence:

```text
[scaffold] Template: /tmp/akka-pack-install-smoke.KjEaTt/.agents/resources/templates/ai-first-saas-starter
[scaffold] Target:   /tmp/akka-pack-scaffold-smoke.sIvxtL
[scaffold] App:      Release Smoke (ai-first-saas-starter)
[scaffold] Package:  ai.first
[scaffold] Files:    291
[scaffold] Wrote specs/scaffold-report.md
[scaffold] Complete
```

Rendered path verification:

```text
verified /tmp/akka-pack-scaffold-smoke.sIvxtL/specs/scaffold-report.md
verified /tmp/akka-pack-scaffold-smoke.sIvxtL/pom.xml
verified /tmp/akka-pack-scaffold-smoke.sIvxtL/src
verified /tmp/akka-pack-scaffold-smoke.sIvxtL/frontend
```
