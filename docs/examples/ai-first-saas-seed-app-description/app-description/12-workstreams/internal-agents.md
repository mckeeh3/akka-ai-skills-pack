# Internal Agents

Internal agents support workstreams and backend flows without becoming left-rail navigation units. Each workstream may maintain an internal workstream agent graph: a virtual internal dashboard that decides which bounded worker can inspect evidence, draft a proposal, classify risk, create a decision-card payload, or report a result back to human dashboard attention.

## User Admin internal workstream agent graph

```text
user-admin-agent internal dashboard
├─ AccessReviewAgent
│  ├─ reads: scoped memberships, invitations, support grants, audit/access-review views
│  └─ writes: review recommendations, dashboard attention, decision-card facts; no autonomous role removal
├─ AdminRiskAgent
│  ├─ reads: proposed admin action, actor AuthContext, target state, policy evidence
│  └─ writes: risk/confidence labels and denial/approval-route advice; no side effects
├─ InvitationDraftAgent
│  ├─ reads: invitation policy, target scope, requested roles, expired/failed delivery evidence
│  └─ writes: invite/resend rationale and email preview; no raw token exposure and no default send side effect
├─ RoleRecommendationAgent
│  └─ writes: least-privilege recommendation or decision-card facts
├─ SupportAccessReviewAgent
│  └─ writes: expiring-grant summary and proposed revoke/extend decision facts
└─ AdminAuditSummaryAgent
   └─ writes: redacted audit/work-trace summary linked to dashboard or audit-trace surface
```

Worker output becomes human-visible only through a governed surface graph edge, dashboard attention item, system-message surface, decision card, or audit trace link.

| Internal agent | Invoked by | Responsibility | Authority boundary | Primary traces/tests |
|---|---|---|---|---|
| `AccessReviewAgent` | User Admin workstream, scheduled access review, admin workflows | Detect stale, risky, orphaned, or expiring access and propose review items. | Read scoped access evidence; propose review items; no autonomous role removal. | recommendation evidence, tenant filtering, decision-card routing. |
| `AdminRiskAgent` | User Admin actions and support-access workflows | Classify privilege-escalation, last-admin, support-access, and risky role changes. | Risk classification only; high-risk effects require human approval. | risk/confidence traces, denial of side effects. |
| `InvitationDraftAgent` | User Admin invite form | Draft invite copy and role rationale; may preview approved Resend templates when granted. | No token visibility; no autonomous invitation creation or email send unless a narrow approved `ToolPermissionBoundary` grants the Resend email tool; default is draft/preview only. | no-token exposure, draft-only behavior, email-tool denial/approval traces. |
| `RoleRecommendationAgent` | User Admin and Access Review surfaces | Suggest least-privilege role/capability changes. | Recommendation/proposal only unless approved by authorized admin. | evidence links, privilege-escalation denial. |
| `SupportAccessReviewAgent` | Support Access surface and timer checks | Summarize support grants nearing expiry and suggest revocation/extension decisions. | No autonomous support grant extension. | support-grant trace and expiry tests. |
| `AdminAuditSummaryAgent` | Admin Audit and Audit/Trace workstreams | Summarize selected audit result sets with trace links and redaction. | Read-only scoped summary; no raw secret/PII exposure beyond caller authority. | redaction and scoped audit query tests. |
| `AgentBehaviorEditorAgent` | Agent Admin and Governance/Policy workstreams | Draft prompt, skill, manifest, tool-boundary, or policy changes with proposed diffs, risk notes, and suggested tests. | May create draft/proposal records; cannot activate or expand authority without approved capability flow. | proposed-diff, authority-expansion denial, activation approval trace tests. |

## Internal-agent rules

- Every internal agent has a governed `AgentDefinition`, approved prompt/skill references, model policy, tool boundary, and trace emission.
- Internal agents receive a service authority basis or delegated `AuthContext`; callers still enforce tenant/customer scope.
- Side-effecting output defaults to proposal, decision card, or workflow advancement unless an accepted bounded policy grants autonomous authority.
