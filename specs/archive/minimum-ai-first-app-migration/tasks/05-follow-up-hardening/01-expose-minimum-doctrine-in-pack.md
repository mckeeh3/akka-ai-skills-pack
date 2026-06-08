# Task: Expose minimum app doctrine in pack metadata and verify installed-pack parity

## Objective

Ensure `docs/minimum-ai-first-saas-app.md` is explicitly exposed by the resource pack and available after installation.

## Scope

- Add `docs/minimum-ai-first-saas-app.md` to `pack/manifest.yaml` `content.references` near the other canonical doctrine docs.
- Verify the build/install path installs the document into `.agents/docs/`.
- If local project `.agents` is intentionally refreshed, do not commit `.agents` files unless repository policy changes; record the command and result in task notes.
- Update any pack README/installed-pack guidance only if it explicitly lists canonical docs and omits the minimum doctrine.

## Required reads

- `specs/minimum-ai-first-app-migration/post-completion-objectives-review.md`
- `docs/minimum-ai-first-saas-app.md`
- `pack/manifest.yaml`
- `tools/build-pack.sh`
- `install.sh`

## Required checks

- `git diff --check`
- `rg -n "minimum-ai-first-saas-app" pack/manifest.yaml pack/README.md pack/AGENTS.md skills/README.md`
- Build/install parity check, preferably with a temporary project:
  - `bash tools/build-pack.sh --clean --no-archive --output-dir /tmp/<tmpdir> --github-repo test/akka-ai-skills-pack`
  - `bash /tmp/<tmpdir>/akka-ai-skills-pack-*/install.sh --location project --project /tmp/<project> --force`
  - `test -f /tmp/<project>/.agents/docs/minimum-ai-first-saas-app.md`

## Acceptance

- The pack manifest explicitly lists the minimum app doctrine.
- A temporary installed pack contains `.agents/docs/minimum-ai-first-saas-app.md`.
- Task changes and queue update are committed.
