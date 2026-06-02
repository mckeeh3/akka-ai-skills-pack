# Pack Release Publication

## Purpose

Prepare the actual `akka-ai-skills-pack` release artifact after the AI-first SaaS starter passed starter-wide release readiness.

This mini-project moves from “release-ready” evidence to package/publication preparation: package metadata, packaged resources, install/scaffold validation, changelog/release notes, downstream handoff, and source-only leakage checks.

## Source context

Builds on:

- `specs/ai-first-saas-starter-release-readiness/`
- `specs/ai-first-saas-starter-release-readiness/starter-release-notes.md`
- `pack/`
- `templates/ai-first-saas-starter/`
- package/install/scaffold scripts and docs

## Scope

- Review/update release/package metadata if applicable.
- Verify packaged resources include the release-ready starter and required docs/scripts.
- Ensure source-only specs and project-only planning assets are not packaged unintentionally.
- Run package/install/scaffold smoke validation.
- Create release changelog/handoff from starter release notes.
- Document downstream install/scaffold instructions and future-work boundaries.

## Non-goals

- Do not add new starter features.
- Do not redo starter release-readiness validation except where needed for package smoke.
- Do not publish externally unless the user explicitly requests an actual external release action.

## Done state

Complete when package metadata/resources are reviewed, package/install/scaffold smoke passes, release changelog/handoff exists, source-only leakage checks pass, and no publication blockers remain for the current repository release scope.
