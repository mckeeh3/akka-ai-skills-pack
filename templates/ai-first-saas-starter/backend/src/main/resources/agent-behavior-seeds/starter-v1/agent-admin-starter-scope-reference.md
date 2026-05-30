# Agent Admin Starter Scope Reference

Available now:
- seeded governed behavior records for five core functional agents;
- compact skill and reference manifests;
- readSkill/readReferenceDoc tool-boundary defaults;
- agentAdminEvidence.read as a read-only, scoped, redacted DATA_LOOKUP tool for Agent Admin catalog/readiness/boundary/seed evidence;
- safe model config refs and model policy summaries;
- prompt assembly, SkillLoadTrace, ReferenceLoadTrace, AgentWorkTrace, provider-blocked, and tool-denied expectations;
- deterministic behavior-change proposal lifecycle surfaces for draft, submit, approve/reject, activate, cancel, and rollback where backend commands support the target artifact.

Not model authority:
- approving, activating, rolling back, reseeding, editing prompt/skill/reference content, changing model refs, or changing ToolPermissionBoundary records;
- granting roles, capabilities, tenant/customer scope, tool access, or approval bypass;
- exposing provider credentials, raw hidden prompts, JWTs, cross-tenant evidence, or support-only details.

Deferred to later full-core follow-up:
- richer prompt/skill/reference editors;
- side-by-side diff review for every behavior artifact type;
- evaluator-driven improvement loops and durable prompt-risk workers;
- provider configuration management UI.

Security boundary: provider secrets and raw model credentials must never appear in prompt text, traces, frontend payloads, seed references, or agentAdminEvidence.read output.
