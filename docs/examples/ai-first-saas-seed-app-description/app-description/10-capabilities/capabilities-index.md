# Capabilities Index

This inventory is reference material for the skills pack's secure AI-first SaaS seed app. In a target project, the equivalent `app-description/10-capabilities/` tree belongs to that project and should be maintained as the application's source of truth.

| Capability id | File | Class | Primary actors/callers | Protected scope | Selected exposure surfaces |
|---|---|---|---|---|---|
| `secure-tenant-user-foundation` | `01-secure-tenant-user-foundation.md` | command, read/evidence, workflow, scheduled, trace/audit | SaaS Owner Admin, Tenant Admin, Customer Admin, Auditor, tenant member, invited user, AI admin assistants, support roles | account, selected tenant/customer, membership, role/permission/capability grants, support-access grant | browser UI actions, HTTP APIs, `/api/me`, admin views, invitation workflow, timed expiry/reminders, email/outbox consumer, scoped admin-assistant tools |
| `ai-first-work-management` | `02-ai-first-work-management.md` | command, workflow, proposal, approval, read/evidence | intent author, supervisor, agent coordinator, specialist agent, exception handler | tenant/customer goal context, delegated authority, policy gates, assignee permissions | browser UI actions, HTTP APIs, workflow steps, agent tools, decision queue, progress views |
| `governance-decisions-audit` | `03-governance-decisions-and-audit.md` | policy/governance, approval, trace/audit, read/evidence | policy owner, reviewer/approver, auditor, outcome owner | tenant/customer governance context, policy ownership, approval authority, audit visibility | governance UI, decision cards, audit views, approval workflow, scoped evidence queries |
| `frontend-shell-integration-patterns` | `04-frontend-shell-and-integration-patterns.md` | read/evidence, command | signed-in user, tenant/customer admin, supervisor, auditor | browser-safe AuthContext, memberships, capabilities, frontend route permissions | authenticated React/Vite shell, typed API client, realtime UI states, navigation/action gating |
| `managed-agent-foundation` | `05-managed-agent-foundation.md` | command, read/evidence, policy/governance, approval, trace/audit | Agent Steward, Tenant Admin, Policy Owner, Reviewer, Auditor, AgentBehaviorEditorAgent, runtime agent invocation service | tenant/customer-scoped AgentDefinition, PromptDocument/PromptVersion, SkillDocument/SkillVersion, AgentSkillManifest, ToolPermissionBoundary, PromptAssemblyTrace, SkillLoadTrace, AgentWorkTrace | agent catalog/detail UI, prompt/skill governance UI, manifest/tool-boundary management, editing-agent proposals, protected HTTP APIs, authorized `readSkill(skillId)`, trace views |

## Capability contract checklist

Each capability file should include enough detail to prevent generation-time invention:

- purpose, in-scope outcomes, and out-of-scope outcomes;
- actors/callers and `AuthContext` with tenant/customer scope, role, permission, and named capability grants;
- input/output schemas, validation, safe denial/error shape, redaction, idempotency, and correlation expectations;
- data access boundaries, side effects, policy/approval/escalation rules, and autonomy level;
- audit/work-trace obligations and retention/redaction expectations;
- selected exposure surfaces or explicit non-exposure;
- links to operating-model, behavior, tests, auth/security, observability, UI, and traceability artifacts.
