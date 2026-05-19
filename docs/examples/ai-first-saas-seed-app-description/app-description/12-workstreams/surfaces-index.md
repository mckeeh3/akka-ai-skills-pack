# Surfaces Index

Structured surfaces are typed renderable artifacts in functional-agent workstreams. They are reusable across agents and every action maps to a backend capability.

| Surface id | Type/version | Primary functional agents | Contract | Key actions/capabilities | Rendering tests |
|---|---|---|---|---|---|
| `access-profile-dashboard` | dashboard/v1 | Access/Profile | `surface-contracts/01-access-profile-dashboard.md` | select context, update profile/settings → `secure-tenant-user-foundation` | `/api/me` state, forbidden context, responsive context selector. |
| `user-admin-command-center` | dashboard+table+forms/v1 | User Admin | `surface-contracts/02-user-admin-command-center.md` | invite/resend/revoke, role membership changes, support-access review → `secure-tenant-user-foundation`, `governance-decisions-audit` | invite lifecycle, last-admin warnings, denied role action. |
| `agent-governance-center` | catalog+diff+approval/v1 | Agent Admin, Governance/Policy | `surface-contracts/03-agent-governance-center.md` | create/draft/review/activate agent behavior records, `readSkill` test → `managed-agent-foundation` | draft/approval states, authority expansion denial, trace links. |
| `mission-control-briefing` | dashboard+timeline+digest/v1 | Mission Control | `surface-contracts/04-mission-control-briefing.md` | create goal, review exception, open decision/trace → `ai-first-work-management`, `governance-decisions-audit` | loading/stale stream, priority attention queue, trace availability. |
| `decision-card` | decision-card/v1 | Mission Control, Governance/Policy, User Admin | `surface-contracts/05-decision-card.md` | approve/reject/counter/defer/escalate → `governance-decisions-audit` | action permissions, conflict after other reviewer acts, evidence rendering. |
| `audit-trace-explorer` | audit-timeline+search/v1 | Audit/Trace, User Admin, Agent Admin | `surface-contracts/06-audit-trace-explorer.md` | search/export scoped traces → `governance-decisions-audit`, `managed-agent-foundation` | redaction, tenant/customer filters, export denial. |

## Surface rules

- Surfaces are the primary UI contracts; conventional routes deep-link to surfaces but do not replace this index.
- Allowed actions are display hints only. Backend capabilities decide authorization, validation, idempotency, audit, and side effects.
- Every surface must define loading, empty, error, forbidden, stale/reconnect, accessibility, and responsive expectations before implementation.
