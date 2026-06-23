# Agent Admin Starter Scope Reference

Available now:
- seeded governed behavior records for five core functional agents;
- compact skill and reference manifests;
- readSkill/readReferenceDoc tool-boundary defaults;
- agentAdminEvidence.read as a read-only, scoped, redacted DATA_LOOKUP tool for Agent Admin catalog/readiness/boundary/seed evidence;
- safe model config refs and model policy summaries;
- prompt assembly, SkillLoadTrace, ReferenceLoadTrace, AgentWorkTrace, provider-blocked, and tool-denied expectations;
- deterministic behavior-change proposal lifecycle surfaces for draft, submit, approve/reject, activate, cancel, and rollback where backend commands support the target artifact;
- structured dashboard, catalog, detail, prompt governance, manifest diff, tool-boundary diff, model refs, seed material/import, test console, prompt-risk review, lifecycle confirmation, and trace surfaces for human review.

Surface routing note: opening or prefilling an Agent Admin surface is safe navigation only. It never edits prompt/skill/reference text, changes tool boundaries/model refs, imports seeds, activates/deactivates agents, or approves behavior changes without the protected backend action.

Not model authority:
- approving, activating, rolling back, reseeding, editing prompt/skill/reference content, changing model refs, or changing ToolPermissionBoundary records;
- granting roles, capabilities, tenant/customer scope, tool access, or approval bypass;
- exposing provider credentials, raw hidden prompts, JWTs, cross-tenant evidence, or support-only details.

Deferred to later full-core follow-up:
- richer prompt/skill/reference editors;
- side-by-side diff review for every behavior artifact type;
- evaluator-driven improvement loops and durable prompt-risk workers;
- provider configuration management UI.

Security boundary: provider secrets and raw model credentials must never appear in prompt text, traces, frontend payloads, starter core templates, or agentAdminEvidence.read output.

Confirmed chat tool plan reference: Agent Admin now has an expanded chat plan catalog with three classification categories:

- Approval-gated: `action-agent-prompt-risk-review-start` / `agent_admin.start_behavior_review_task` / `schema.agent-admin.prompt-risk-review.start.v1`. Exact plan snapshot confirmation leaves the step approval-gated; it cannot change, approve, activate, roll back, or reseed behavior artifacts.

- Chat-proposal-only simulation and test (no side effects): `action-agent-detail-run-test`, `action-agent-prompt-governance-simulate`, `action-agent-skill-manifest-simulate`, `action-agent-tool-boundary-simulate`, `action-agent-model-refs-run-test`. Provider missing returns a blocked result; no fake successful normal output is produced. No activation-without-approval guardrail applies to all paths.

- Chat-proposal-only submit-review (no authority): `action-agent-prompt-governance-submit-review`, `action-agent-skill-manifest-submit-review`, `action-agent-tool-boundary-submit-review`, `action-agent-model-refs-submit-review`, `action-propose-prompt-diff`, `action-submit-behavior-change`, `action-agent-behavior-proposal-submit`. Text in proposal input cannot grant authority; visible agent definition must exist; no active behavior change until a separate approval and activation command completes.
