# Capability: Policy Governance

This is a lightweight capability contract for future refinement. It records the governed boundary for proposing, reviewing, simulating, activating, rolling back, and auditing DCA policy changes without inventing final policy schemas or evaluator thresholds.

## Capability definition

- capability-id: `policy-governance`
- capability number: `CAP-08`
- class: policy/governance, proposal, approval, read/evidence, trace/audit
- purpose: manage behavior-changing rules, thresholds, examples, prompts, skills, and authority boundaries as reviewed, versioned, auditable governance objects.
- business outcome: agents can suggest improvements from decisions and outcomes, but human policy owners control activation of changes that affect autonomy, permissions, approvals, or customer/business impact.

## In-scope outcomes

- Create policy proposals from human requests, repeated exceptions, decisions, evaluator findings, or outcome loops.
- Review proposed clause, threshold, prompt, skill, reference-example, or authority changes with evidence, risk, affected capabilities, and alternatives.
- Simulate or replay material changes against representative historical traces where supported.
- Approve, reject, modify, activate, deprecate, or roll back policy versions with durable records.
- Expose active policy versions and citations for workflows, agents, and decision cards.

## Out-of-scope outcomes

- Automatic policy activation from agent output, evaluator scores, or a single decision outcome.
- Final policy-document schema, simulation methodology, evaluator rubric, or threshold values beyond existing examples.
- Bypassing foundation role/capability checks for policy owners, reviewers, or support users.

## Authority and contract

- actors/callers: policy owner, dealer owner, operations supervisor, governance reviewer, policy proposal agent, evaluator agent, workflow callers, auditor.
- AuthContext/scope: authenticated account, selected tenant/customer or policy-family context, active membership, policy propose/review/activate/rollback permissions, and tenant/customer/policy filters.
- inputs: policy proposal, affected capability ids, policy family, clause/threshold/prompt/skill/reference-example draft, evidence/trace links, simulation request/result reference, reason, correlation id, and idempotency key.
- outputs: proposal status, diff summary, active/previous version references, simulation/evidence summary, decision-card link, safe denial shape, and trace links.
- side effects: proposal workflow, simulation request, decision card, policy commit, activation/deprecation/rollback fact, notification, versioned document update, and audit/work trace.
- idempotency: duplicate proposal/activation for the same policy family/version/change hash returns existing proposal or commit result.
- policy/approval: humans approve activation; agents may draft, compare, summarize, simulate, or recommend only within granted tool permissions.
- exposure surfaces: governance UI, HTTP APIs, policy review/approval workflow, simulation/evidence views, decision cards, scoped proposal tools, audit traces, and policy read surfaces for workflows/agents.

## Required future detail

- Exact versioned policy, prompt, skill, reference-example, simulation, and rollback schemas.
- Evaluator/rubric definitions and accepted replay data boundaries.
- Activation propagation and compatibility rules.
- Concrete tests for proposal-only behavior, approval authority, rollback, duplicate commits, tenant isolation, audit, redaction, and agent tool limits.

## Linked layers

- operating model: `../15-operating-model/policies-and-approval-gates.md`, `../15-operating-model/decisions-exceptions-and-evidence.md`, `../15-operating-model/outcomes-and-learning-loops.md`
- behavior: `../20-behavior/flows/02-lifecycle-and-exception-flows.md`
- auth/security: `../40-auth-security/authorization-rules.md`, `../40-auth-security/agent-permissions.md`
- observability: `../50-observability/audit-trace-and-outcomes.md`
- UI: `../55-ui/ui-surfaces.md`
- tests: future test refresh under `../30-tests/`
