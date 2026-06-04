# Package Resource Review

Task: `TASK-PRP-01-001`
Date: 2026-06-02

## Scope reviewed

- `pack/manifest.yaml`, `pack/README.md`, `pack/AGENTS.md`, and `pack/EXAMPLES-README.md`
- root `install.sh`
- release/package scripts: `tools/build-pack.sh`, `tools/release.sh`, `tools/install-release-template.sh`, `tools/check-version-consistency.sh`
- starter scaffold command: `tools/scaffold-ai-first-saas-starter.sh`
- starter template resources under `templates/ai-first-saas-starter/`
- install/bundle behavior using a temporary build and temporary project install

## Package metadata and inclusion findings

- Manifest metadata remains coherent for the current package line:
  - `metadata.name`: `akka-ai-skills-pack`
  - `metadata.version`: `0.3.0`
  - `compatibleAkkaSdk`: `3.5.x`
- `tools/check-version-consistency.sh` passes for `README.md`, `pack/README.md`, and the manifest version.
- The package model includes the expected pack-facing guidance and resources:
  - installed-pack `AGENTS.md` from `pack/AGENTS.md`
  - skills and skill routing map
  - repository docs
  - Java reference examples
  - frontend workstream reference examples
  - starter template resources under `resources/templates/ai-first-saas-starter/`
  - executable scaffold command under `.agents/bin/scaffold-ai-first-saas-starter.sh`
- The installer and pack README both describe the starter scaffold path and the explicit scaffold command rather than materializing starter application code during a default skills-pack install.

## Resource correction made

Initial package build review failed because `tools/build-pack.sh` detected that `frontend/src` was out of sync with `templates/ai-first-saas-starter/frontend/src`.

Correction applied in this task:

- synchronized `frontend/src` from the validated starter template frontend source so the exported frontend reference examples match the starter template source expected by the package build guard.
- kept the newly shared contract tests runnable from both source-repo `frontend/src` and template `templates/ai-first-saas-starter/frontend/src` locations by allowing source-repo frontend tests to read backend evidence from the starter template backend path.

This keeps the package's frontend reference resources aligned with the release-ready starter template, keeps source-repo frontend validation runnable, and allows the package bundle to build.

## Focused validation evidence

Commands/checks run:

```bash
bash tools/check-version-consistency.sh
diff -qr --exclude node_modules --exclude .env.local frontend/src templates/ai-first-saas-starter/frontend/src
bash tools/build-pack.sh --output-dir "$TMPDIR" --github-repo mckeeh3/akka-ai-skills-pack --no-archive
bash install.sh --location project --project "$TMPPROJ" --force
npm --prefix frontend run typecheck
npm --prefix frontend test
```

Temporary bundle review summary:

- top-level bundle paths: `BUILD-INFO.txt`, `BUNDLE-README.md`, `LICENSE`, `README.md`, `docs`, `frontend`, `install.sh`, `pack`, `pom.xml`, `skills`, `src`, `templates`, `tools`
- starter template files present: `README.md`, `TEMPLATE-MANIFEST.md`, `backend/pom.xml`, `frontend/package.json`
- scaffold script present in bundle: `tools/scaffold-ai-first-saas-starter.sh`
- `install.sh` contains starter/scaffold/template references
- `pack/README.md` contains starter/scaffold/template references

Temporary installed-pack review summary:

- installed top-level paths: `AGENTS.md`, `bin`, `docs`, `manifests`, `resources`, `skills`
- starter template installed: yes
- scaffold command installed and executable: yes
- pack manifest installed: yes

## Leakage boundary scans

Temporary bundle scan:

- source-only `specs/` outside `templates/ai-first-saas-starter/specs`: `0`
- `akka-context` paths bundled: `0`
- `node_modules` or `.env.local` bundled: `0`

Temporary installed-pack scan:

- source-only `specs/` outside `resources/templates/ai-first-saas-starter/specs`: `0`
- `akka-context` paths installed: `0`
- `node_modules` or `.env.local` installed: `0`

The starter template's own `specs/` seed remains intentionally packaged as scaffold resource material.

## Conclusion

Package metadata, installer copy rules, starter template inclusion, frontend reference synchronization, and source-only leakage boundaries are acceptable for the current release-publication scope. No package-resource blocker remains from this review.
