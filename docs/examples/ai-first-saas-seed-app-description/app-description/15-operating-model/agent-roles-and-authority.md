# Agent Roles and Authority

## Work-management agents

- coordinator agent:
  - responsibility: turn a goal into a draft plan, identify needed tools/data, assign specialist steps, summarize progress
  - authority: draft only until launch; may execute low-risk steps after plan approval if permissions allow
- specialist agent:
  - responsibility: perform bounded analysis, classification, summarization, recommendation, or tool-backed task
  - authority: limited to assigned task permissions
- evaluator/guardrail agent:
  - responsibility: evaluate outputs against policy, confidence, risk, completeness, and evidence sufficiency
  - authority: recommend pass/fail/escalate; cannot override human approvals
## Mandatory admin offload agents

- AccessReviewAgent:
  - responsibility: identify stale invitations, dormant access, risky role combinations, expiring support access, orphaned admin coverage, and last-admin risk from scoped admin views
  - authority: create access-review recommendations and low-risk tasks; cannot change access directly
- AdminRiskAgent:
  - responsibility: score proposed role, membership, support-access, identity relink/reset, tenant suspension, and bulk admin actions
  - authority: produce risk/evidence/confidence and route high-risk changes to decision cards
- InvitationDraftAgent:
  - responsibility: draft invite copy, onboarding notes, bulk invite drafts, and role rationale
  - authority: never exposes raw tokens and never sends high-impact bulk invitations without approved policy path
- RoleRecommendationAgent:
  - responsibility: recommend least-privilege roles/capabilities for invited or existing users
  - authority: recommendation only; admin grants or expanded roles require authorized human action and, when risky, decision-card approval
- SupportAccessReviewAgent:
  - responsibility: review support-access grants, expiry, usage, purpose, and revoke candidates
  - authority: recommend expiry/revocation/renewal; broad support-access expansion requires approval
- AdminAuditSummaryAgent:
  - responsibility: summarize admin audit/search result sets for supervisors and auditors
  - authority: summary only with redaction and audit trace links

## Non-responsibilities

- agents do not create users, change tenant roles, alter tenant isolation, remove last admins, bulk disable users, expand support access, reset/relink identity subjects, or commit expanded policy authority autonomously
- required traces:
  - prompt/skill/policy version used
  - tool/data access requested and granted
  - recommendation, confidence, risk, evidence, and escalation reason
