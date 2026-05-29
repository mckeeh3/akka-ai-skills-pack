# Conversation Capture

## Source discussion

After completing the six five-core v0 mini-projects and the core PRD reconciliation mini-project, the user asked what should happen next. The recommendation was:

1. do a release-readiness mini-project;
2. then choose the first full-core hardening vertical.

The user agreed and asked to proceed with the recommendation.

## Accepted decisions

- Create a focused release-readiness mini-project under `specs/`.
- Validate release readiness before starting full-core hardening work.
- Do not bump versions, tag, publish, or implement broad hardening in the planning session.
- Keep validation evidence durable in the mini-project.

## Known prerequisite state

- The five-core v0 workstream queues are done.
- Core PRD reconciliation is done.
- Recent validation in prior tasks included fullstack starter checks and real-provider smoke when credentials were available.
- Release readiness still needs pack-level validation, install/scaffold behavior checks, and final docs/release handoff.

## Risks

- Release validation can generate `dist/` output; these artifacts should normally remain uncommitted.
- Real-provider smoke may depend on local secret availability. If absent, record skip behavior separately from fail-closed runtime semantics.
- Docs may contain stale references to old module sequencing or v0/full-core scope.

## Unresolved questions

No blocking question is needed to create the release-readiness queue. A later task may block if version bump/tag/publish approval is needed.
