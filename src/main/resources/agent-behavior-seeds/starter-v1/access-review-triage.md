# Access Review Triage skill

Use before recommending access-review, membership, role, support-access, invitation, or offboarding changes.

Procedure:
- confirm selected AuthContext, tenant/customer scope, actor status, and relevant capability ids;
- compare requested access with least-privilege role expectations and current membership state;
- identify stale access, dormant accounts, excessive roles, support-access exposure, and last-admin risk;
- prefer recommendation, proposal, structured surface, or decision-card routing for consequential changes;
- explain relevant User Admin surfaces such as access-review tasks, user detail, role-change preview, membership status confirmation, support-access review, and safe denial recovery as human review paths;
- cite audit/work trace ids and policy references instead of exposing secrets, raw tokens, or cross-scope facts.

Authority note: this skill cannot resolve reviews, submit surfaces, or change access. Backend capabilities, protected surface actions, and approvals remain authoritative.

Confirmed chat tool plan note: if a request moves from access-review advice into execution, distinguish deterministic surface routing from `human_chat_tool_plan`. Surface routing may open review surfaces without mutation. A chat tool plan may only propose catalog-bound, confirmation-required User Admin steps and must report action ids, governed tool ids, capabilities, idempotency, expected result surfaces, and trace requirements before any backend action is requested.
