# Test Index

- acceptance:
  - `acceptance/01-seed-app-acceptance.md`
- regression:
  - `regression/01-tenant-isolation-and-idempotency.md`
- negative:
  - `negative/01-forbidden-actions.md`
- operational:
  - `operational/01-observability-and-audit.md`

## Workstream expertise coverage

Functional-agent readiness requires tests that cover the active workstream expert bundle or an explicit deferral that prevents that agent/workstream from being reported ready.

For each LLM-enabled functional agent, test coverage must include:

- the agent's authoritative bundle under `12-workstreams/workstream-expertise/` or an explicit readiness-impacting deferral/non-LLM status;
- compact expertise manifest assembly with assigned skill/reference ids and no full bodies in default prompt context;
- allowed `readSkill(skillId)` and reference-document loads for assigned active documents;
- denied unassigned, inactive, cross-tenant, disabled-agent, oversized, or unauthorized mode loads;
- `ToolPermissionBoundary` denial for loaders and other model-facing tools outside the bundle;
- proof that skill/reference text cannot expand roles, tenant scope, approval rights, tools, or backend capabilities;
- capability authorization and audit behavior for agent-requested or agent-proposed actions;
- surface rendering of manifest summaries, evidence, denials, decision cards, and trace links where those surfaces are in scope;
- `PromptAssemblyTrace`, `SkillLoadTrace`, reference-load trace, `AgentWorkTrace`, data-access, decision, and audit emission for allowed and denied work.

My Account coverage must additionally exercise `workstream-expertise/my-account-agent.md`: `/api/me` and selected-context explanation, own profile/settings draft and save paths, disabled-user/inactive-membership/forbidden-context denials, raw-token/provider-secret denial, assigned and denied skill/reference loads, missing `read_skill` and `read_reference` denials, no authority expansion from profile/settings/prompt/reference text, `my-account-dashboard` rendering, and trace emission.

Agent Admin coverage must additionally exercise `workstream-expertise/agent-admin-agent.md`: governed behavior artifact proposals, prompt/skill/reference diffs, manifest and tool-boundary proposals, seed-upgrade proposals, approval/rejection/activation/rollback gates, missing `read_skill` and `read_reference` denials, authority-expansion denial, and trace visibility in `agent-governance-center`, `decision-card`, and `audit-trace-explorer`.

Mission Control coverage must additionally exercise `workstream-expertise/mission-control-agent.md`: active goal/plan briefing, delegated-work progress triage, exception routing, approval queue review, outcome-signal review, retained-human-authority denial, assigned and denied skill/reference loads, missing `read_skill` and `read_reference` denials, no authority expansion from supervisory text, `mission-control-briefing` and `decision-card` rendering, and work/decision/outcome trace emission.

Governance/Policy coverage must additionally exercise `workstream-expertise/governance-policy-agent.md`: policy-clause review, approval-gate proposal, simulation/replay evidence, activation/rollback approval gates, authority-expansion denial, assigned and denied skill/reference loads, missing `read_skill` and `read_reference` denials, policy text cannot grant backend authority, and trace visibility in `agent-governance-center`, `decision-card`, and `audit-trace-explorer`.

Audit/Trace coverage must additionally exercise `workstream-expertise/audit-trace-agent.md`: scoped trace search/detail/explanation, redaction-preserving summaries, sensitive evidence handling, support-access audit, assigned and denied skill/reference loads, missing `read_skill` and `read_reference` denials, tool-boundary denials, no authority expansion from trace/export text, export eligibility/partial/approval-required/denied paths, forbidden-filter non-enumeration, tenant/customer isolation, and trace visibility in `audit-trace-explorer` and `decision-card`.
