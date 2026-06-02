# Conversation Capture: Core App Feature Completion

## User goal

The user asked which core-app features were not yet implemented, then requested a mini-project to implement those features.

## Current agreed interpretation

- “Core app” means the repository starter/core SaaS app under `templates/ai-first-saas-starter/` and its app-description/reference/runtime assets.
- The current starter is release-ready only for the documented full-core SMB starter scope.
- The follow-up project should implement currently documented remaining slices and post-release candidates without weakening runtime completion standards.

## Features identified as not yet implemented or not claimed complete

1. Event-sourced invitation lifecycle history.
2. Richer event-sourced lifecycle history for governed runtime artifacts: `PromptDocument`, `SkillDocument`, `ReferenceDocument`, manifests, and `ToolPermissionBoundary`.
3. Broader core projections/views: `InvitationView`, `UserDirectoryView`, `AdminAuditView`, governed-agent projections, and extended foundation projections.
4. Broader workstream/event coverage beyond bounded v3 starter event families.
5. Notification delivery channels beyond the current in-app center and existing email boundary: SMS, mobile push, webhook, Slack, Teams, and broad notification analytics/platform capabilities.
6. Broader task/AutonomousAgent notification coverage beyond currently implemented worker states and verticals.
7. Future digest/export platform beyond My Account personal attention digest and Audit/Trace summary workers.
8. Future policy simulation platform beyond Governance/Policy impact analysis.
9. Enterprise extensions: IAM/SCIM/SSO administration, SIEM/legal hold/e-discovery, compliance suites, marketplace prompts, arbitrary tenant-managed tool binding, and policy-as-code authoring.
10. Non-blocking polish: mobile/off-canvas rail QA, final rendered production asset scans after source changes, and frontend bundle-size optimization.

## Accepted constraints

- Do not claim a generated-app runtime feature complete without a real rendered local Akka/API/UI validation path at the stated scope.
- Missing provider configuration must fail closed with actionable system messages/errors, traces, and no fake success.
- Fixtures/test doubles are allowed only in tests or explicit local/demo modes.
- Do not silently expand the starter release scope without updating README/app-description/readiness docs and validation evidence.
- Do not migrate normal user-facing request/response workstream turns away from `WorkstreamRuntimeAgent` by default.

## Rejected alternatives

- A single “implement all missing features” mega-task.
- Treating enterprise/provider integrations as complete through mock or captured-only adapters.
- Implementing provider-specific SMS/push/Slack/Teams production delivery without recording provider choices.

## Risks

- External delivery channels and enterprise integrations can sprawl into unbounded platform work.
- Policy simulation, marketplace prompts, and arbitrary tenant-managed tool binding can create security/authority risks if prompt/skill/tool text is allowed to grant authority.
- Broader event/projection work can regress current release-ready behavior if not validated through rendered scaffolds.

## Unresolved decisions

See `pending-questions.md` for provider-specific and enterprise scope choices. These questions should block only the affected provider-specific tasks, while provider-neutral capability/platform foundations can proceed with fail-closed boundaries.
