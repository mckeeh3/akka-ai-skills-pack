# Core Feature Completion Build Backlog

## Goal

Implement the documented not-yet-implemented core-app features as bounded, validated increments in the starter template.

## Work breakdown

### Durable core

1. Add invitation event history while preserving existing invitation runtime/API/UI behavior.
2. Add governed artifact lifecycle history for prompt, skill, reference, manifest, and tool-boundary records.
3. Complete core projections and view contracts used by User Admin, Agent Admin, Audit/Trace, and Governance/Policy.

### Events and notifications

4. Expand workstream event families and projection-refresh events beyond the current bounded v3 set.
5. Add notification delivery-channel platform foundations, including channel preferences, delivery attempts, redaction, audit, and provider-neutral fail-closed outboxes.
6. Add provider-specific external channel adapters only after `pending-questions.md` resolves provider choices.

### Workers, digest/export, and governance

7. Broaden AutonomousAgent task notification coverage across implemented and future worker categories.
8. Implement digest/export platform extensions with authorization, redaction, audit, and scheduled/manual paths.
9. Implement policy simulation with proposals/results/decision surfaces and no automatic authority expansion.

### Enterprise/admin extensions

10. Add IAM/SCIM/SSO admin foundation contracts and local validation.
11. Add SIEM/legal hold/e-discovery/compliance export foundations.
12. Add marketplace prompt and tenant-managed tool-binding governance foundations.

### Polish and validation

13. Run mobile/off-canvas rail QA, rendered asset scan automation, bundle-size optimization, and documentation alignment.
14. Run terminal verification and append follow-up tasks if gaps remain.

## Required validation themes

- Rendered scaffold `tools/validate-ai-first-saas-starter-fullstack.sh` for broad changes.
- Focused backend tests in rendered scaffolds for Akka/component behavior.
- Frontend tests/typecheck/build for surface or UI changes.
- Secret-boundary scans for provider, notification, prompt/skill/reference, export, and static-asset changes.
- Provider-missing fail-closed checks for external integrations.

## Acceptance criteria

The backlog is complete when every feature slice is implemented, explicitly deferred/blocked with scope impact, or superseded by a later accepted plan; current release-ready behavior remains validated; and terminal verification records no material unqueued gaps.
