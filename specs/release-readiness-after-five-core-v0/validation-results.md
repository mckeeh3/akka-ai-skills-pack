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
[check-version] README.md and pack/README.md version references match manifest version 0.2.10
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
[build-pack] Cleaning previous outputs for akka-ai-skills-pack-0.2.10
[build-pack] Building akka-ai-skills-pack-0.2.10
[build-pack] GitHub repo: mckeeh3/akka-ai-skills-pack
[build-pack] Bundle directory:  /home/hxmc/ai/akka-ai-skills-pack/dist/akka-ai-skills-pack-0.2.10
[build-pack] Release installer: /home/hxmc/ai/akka-ai-skills-pack/dist/install-akka-ai-skills-pack-0.2.10.sh
[build-pack] Done
```

#### Diff whitespace check

Command:

```bash
git diff --check
```

Result: passed with no output.
