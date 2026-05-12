# Security Guide for AI Agent-First Applications

This guide distills the security and product architecture lessons from `agent-security.txt` into implementation guidance for AI coding agents building agent-first applications.

## Source caveat

The source transcript is useful conceptual guidance, but it should not be treated as independently verified security research. Specific vendor claims, model capability claims, dates, anecdotes, and product examples from the transcript should be considered illustrative unless separately validated. The architectural recommendations in this document should be adapted to your own threat model, compliance requirements, product context, and empirical testing.

## Core principle

Agents should not be trusted to both pursue a task and police whether that task is allowed. Treat agents as managed workers: they need assignment, context, permissions, supervision, correction, and an auditable work record.

The key pattern is an **action-boundary judge**: before an agent performs meaningful side effects, a separate validator decides whether the proposed action is aligned with user intent, policy, and available evidence.

## Why prompts and blanket approvals are insufficient

Do not rely on the acting agent's prompt alone for safety-critical authorization. Long-running agents optimize for task completion, and safety instructions can degrade across long contexts or conflict with the primary goal.

Do not require manual approval for every action either. That creates approval fatigue and teaches users to click through prompts without meaningful review.

Instead, classify actions by risk and apply the right control level.

## Action risk classes

### 1. Read-only actions

Examples:
- Search
- Retrieve records
- Summarize documents
- Inspect code or logs

Default control:
- Usually allowed without heavy review.
- Add validation if sensitive data is involved or if read access can enable later unsafe writes.

Implementation guidance:
- Enforce least-privilege read scopes.
- Log access to sensitive resources.
- Prevent read tools from silently escalating into write-capable flows.

### 2. Reversible internal writes

Examples:
- Drafts
- Labels
- Internal notes
- Local files
- Non-destructive status updates

Default control:
- Validate when shared internal systems are affected.
- Prefer reversible operations over destructive ones.

Implementation guidance:
- Use drafts instead of sends.
- Use archive/soft-delete instead of hard-delete.
- Track actor, timestamp, input context, and rollback path.

### 3. External-impact actions

Examples:
- Sending emails or messages
- Booking meetings
- Posting publicly
- Opening pull requests
- Notifying customers

Default control:
- Must pass through a strong judge layer before execution.

Implementation guidance:
- Require an explicit action proposal.
- Verify user intent and recipient/customer impact.
- Check policy, attachments, data sensitivity, and current context.
- Allow the judge to approve, block, request revision, or escalate.

### 4. High-risk actions

Examples:
- Spending money
- Deleting data
- Changing permissions
- Merging code
- Submitting legal, financial, compliance, or security-sensitive work

Default control:
- Judge plus human approval unless a very narrow, explicit automation policy exists.

Implementation guidance:
- Require policy-backed authorization.
- Use hard guardrails in code, not just prompts.
- Require audit logs.
- Prefer multi-party or role-based approval for irreversible actions.
- Implement rate limits, spend limits, and blast-radius limits.

## Action proposal format

Before any non-trivial side effect, the acting agent should produce a structured proposal for the judge.

```json
{
  "action_type": "send_email | create_pr | update_record | delete_data | spend_money | ...",
  "risk_class": "read_only | reversible_write | external_impact | high_risk",
  "intended_tool": "tool or API name",
  "target_resource": "record, file, account, recipient, repository, etc.",
  "user_intent": "what the user asked for",
  "agent_goal": "what the acting agent is trying to accomplish",
  "proposed_change": "specific action to perform",
  "evidence": [
    "messages, tickets, documents, policies, code references, or other support"
  ],
  "authorization_basis": "why this agent believes it is allowed",
  "expected_external_effect": "who or what will be affected",
  "reversibility": "none | easy | partial | difficult",
  "rollback_plan": "how to undo or mitigate the action",
  "sensitive_data_involved": true,
  "uncertainties": [
    "open questions or assumptions"
  ]
}
```

## Judge decision scope

The judge should not be limited to yes/no. Production systems need at least four outcomes:

1. **Allow** — the action is aligned with user intent and policy.
2. **Block** — the action is unsafe, unauthorized, or unsupported.
3. **Revise** — the action may proceed after changes, such as drafting instead of sending or removing an attachment.
4. **Escalate** — route to a human, owner, legal, security, or another higher-trust process.

Example decision:

```json
{
  "decision": "revise",
  "reason": "The user asked for a pricing deck, but the selected deck contains non-public enterprise pricing.",
  "required_changes": [
    "Use the public pricing deck",
    "Create a draft instead of sending directly"
  ],
  "escalation_target": null
}
```

## Where to place the judge

Place validation at the **action boundary**:

- Before a tool call with side effects
- Before writes or deletes
- Before sending messages
- Before modifying permissions
- Before spending money
- Before publishing, merging, or notifying others

The acting agent may plan freely, but execution must go through the control layer.

## Implementation checklist for coding agents

When implementing an agent-first feature, add:

- [ ] A registry of tools and their risk classes
- [ ] Separate read and write permissions
- [ ] Structured action proposals for write/external/high-risk actions
- [ ] A judge/validator interface independent from the acting agent
- [ ] Four-way judge outcomes: allow, block, revise, escalate
- [ ] Human approval path for high-risk actions
- [ ] Audit logs for proposals, decisions, tool calls, and outcomes
- [ ] Rollback or mitigation path for reversible actions
- [ ] Tests for unauthorized, ambiguous, stale, and over-broad actions
- [ ] Metrics for judge approval, block, revision, escalation, and false-positive rates
- [ ] Versioning for judge prompts/models/policies
- [ ] Safe defaults: deny or draft when authorization is unclear

## Design rules

- Do not let the same agent be the primary task executor and final authority on safety.
- Do not treat conversation replies as automatic authorization for broader future actions.
- Do not infer permission from stale context when the action has real-world consequences.
- Do not expose high-risk tools to agents unless guarded by explicit policy and approval.
- Prefer narrower tools over broad administrative APIs.
- Prefer drafts, previews, and reversible states before irreversible execution.
- Treat agent-written memory as untrusted until validated.
- Store enough evidence to explain why an action was allowed or denied.

## Testing scenarios

Include tests such as:

1. Agent attempts to send an email without explicit user authorization.
2. Agent selects an outdated or confidential attachment.
3. Agent attempts to delete instead of archive.
4. Agent tries to merge code after tests pass but without approval.
5. Agent treats a third-party reply as permission to continue an unrelated workflow.
6. Agent attempts to spend above policy limits.
7. Judge requests revision rather than blocking useful work entirely.
8. Human escalation is triggered for high-risk ambiguous actions.

## Metrics to track

Track the judge as a product component:

- Approval rate by action class
- Block rate by action class
- Revision rate
- Escalation rate
- Human override rate
- Post-action incident rate
- False approvals
- False blocks
- Time added by validation
- Actions bypassing validation

## Recommended default policy

If authorization is unclear:

- Read-only: allow if least-privilege and non-sensitive.
- Reversible internal write: draft, label, or soft-write with audit log.
- External impact: require judge approval.
- High-risk: require judge approval and human approval.

When in doubt, choose the safer reversible action: draft instead of send, archive instead of delete, propose instead of execute, request approval instead of assuming permission.
