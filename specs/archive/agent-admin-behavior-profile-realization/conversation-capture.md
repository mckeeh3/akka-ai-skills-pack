# Conversation capture: Agent Admin behavior profile realization

## User input

The user said:

> the my-account agent admin is ready to compile. the existing code is very stale as it was implemented using an older version of the skills-pack/ and the app-description/ has been significantly updated.

A bounded compile pass repaired small My Account drift and recorded Agent Admin as stale. The user then asked:

> let's use a mini-project do the work needed to complete the code changes

## Accepted interpretation

- Target is root app realization, not `skills-pack/**` maintenance.
- Agent Admin app-description is ready enough to compile, but existing code and tests must be treated as stale implementation evidence.
- Use a durable mini-project under `specs/` and execute queued work one fresh-context task at a time via parent/subagent orchestration.
- Planning only in this run; do not implement queued tasks unless explicitly asked after the queue exists.

## Key current-intent decisions from app-description

- Agent Admin is SaaS Owner/Admin-only.
- Generated agents and generated tool implementations are static/code-generated from app-description and cannot be created/deleted/edited in Agent Admin.
- Agent Admin manages runtime behavior profiles, prompts, independently managed skills, governed references, model config references, skill assignments, generated tool assignments, and safe trace visibility.
- Editing is AI-assisted and proposal-first. The editing agent drafts structured proposals with full proposed content, diff, rationale, risk classification, authority-expansion flags, and suggested tests.
- Save Draft creates a non-active immutable proposal/version. Activation is a separate protected backend action.
- Low-risk copy/clarity changes may be reviewed and activated by the same authorized SaaS admin as the foundation simplification.
- Medium/high-risk, authority-expanding, model-policy, tool-boundary, approval-boundary, or tenant-scope changes must deny direct activation or route to review/decision-card workflow.
- Runtime behavior changes only after activation. Historical versions are read-only. Restore creates a proposal.
- Tenant-specific behavior changes create tenant-scoped behavior profile versions; SaaS owners use reserved `saas-app-owner` scope.
- Runtime loading resolves tenant profile fallback to global, active prompt/version, compact assigned skill/reference manifests, model policy, selected AuthContext, allowed generated tools, and tool-boundary decisions.
- Runtime traces must expose safe metadata without full skill/reference content or provider secrets.

## Known stale implementation themes to address

- Direct save/restore/create/delete active mutation paths.
- Missing proposal review and activation state model.
- Missing tenant-scoped behavior profile version seams.
- Stale whole-agent profile mutation and lifecycle UI/actions.
- Partial catalog/detail fields and filters.
- Incomplete assignment/model/profile history surfaces.
- Runtime traces narrower than current intent.
- Tests and fixtures that still assert direct active save, permanent delete, and old governance-console assumptions.

## Planning decision

Create `specs/agent-admin-behavior-profile-realization/` with sprints, backlog, task briefs, `pending-tasks.md`, and a terminal verification loop. Future execution must use `pi-subagents` sequentially: one fresh-context worker subagent for one queued task at a time, each task updating status, running checks, committing, and reporting the next runnable task.
