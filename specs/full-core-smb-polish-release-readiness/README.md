# Full-Core SMB Polish and Release Readiness

## Purpose

Validate the AI-first SaaS starter as one coherent SMB full-core baseline after the individual full-core workstream mini-projects have completed.

This mini-project focuses on integrated polish and release readiness across the five core workstreams, shared shell, visual UX, provider/runtime fail-closed behavior, trace links, frontend/backend consistency, documentation, packaging, and release handoff.

## Background

The full-core SMB hardening wave has completed the core workstream queues:

- `specs/full-core-smb-baseline-and-ux/`
- `specs/full-core-smb-user-admin/`
- `specs/full-core-smb-user-admin-access-management/`
- `specs/full-core-smb-user-admin-agent-guidance/`
- `specs/full-core-smb-user-admin-access-review-worker/`
- `specs/full-core-smb-agent-admin/`
- `specs/full-core-smb-audit-trace/`
- `specs/full-core-smb-governance-policy/`
- `specs/full-core-smb-my-account/`

The next step is not another workstream feature. It is to prove the starter works as a coherent SMB product baseline and to prepare a release-quality handoff.

## Scope

- Integrated fullstack validation for `templates/ai-first-saas-starter/`.
- Cross-workstream shell/action/navigation/trace-link consistency.
- Visual UX consistency review across dashboards, detail surfaces, task surfaces, system messages, and provider-blocked states.
- Provider fail-closed and no deterministic/model-less normal-runtime substitute verification.
- Frontend/backend fixture/runtime contract alignment.
- Static secret/no-hidden-prompt/no-provider-secret verification for browser-visible assets.
- README/template/docs/release guidance updates needed for users to understand the SMB full-core baseline.
- Release handoff summary with validation results, known intentional deferrals, and next recommended post-release projects.

## Non-goals

- Do not add new full-core workstream feature scope unless validation finds a release-blocking gap.
- Do not implement deferred worker candidates merely because they were identified; keep them as post-release follow-up unless required for release correctness.
- Do not relax runtime completion doctrine or accept fixture-only/model-less success paths for model-backed features.
- Do not broaden SMB scope into enterprise IAM, SIEM, compliance suites, marketplace, or policy-as-code authoring.

## Target source areas

- `templates/ai-first-saas-starter/`
- root `frontend/` mirrors if repository convention requires synchronization
- starter README/docs/package guidance
- validation scripts under `tools/`
- full-core SMB specs and release handoff artifacts

## Execution model

Execute one task per fresh harness session. Start with an integrated release-readiness map that inspects validation, UX, docs, packaging, and known deferrals. Append bounded fix/review/documentation tasks from that map.

## Done state

This mini-project is complete when:

- the full starter passes broad validation or release blockers are explicitly bounded and fixed;
- cross-workstream actions, trace links, surfaces, provider failures, and denials are coherent;
- visual UX meets the shared quality standard at SMB scope;
- docs and handoff material describe the actual runtime, provider configuration, fail-closed behavior, and intentional deferrals;
- no browser-visible secrets, provider credentials, hidden prompts, or unauthorized evidence are found;
- a release handoff summarizes validation commands/results, known non-blocking follow-ups, and release recommendation.
