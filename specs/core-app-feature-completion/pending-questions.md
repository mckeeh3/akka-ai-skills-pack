# Pending Questions: Core App Feature Completion

## Queue rules

- Ask one question at a time unless the user requests a batch.
- Resolve `answered` questions by reconciling them into the relevant artifacts.
- Do not execute implementation tasks blocked by unresolved `blocking` questions.
- Preserve question IDs; supersede obsolete questions rather than deleting them.

## Questions

### Q-001: Production notification providers

- status: pending
- priority: blocking
- category: integration
- depends on: []
- blocks:
  - provider-specific SMS, mobile push, Slack, and Teams production adapter tasks
- source:
  - conversation identified future SMS/mobile-push/webhook/Slack/Teams delivery channels
- question: >
    Which production providers should the starter support for SMS, mobile push, Slack, and Teams, if any, versus only implementing provider-neutral fail-closed adapter seams and captured local/test outboxes?
- why it matters: >
    Runtime completion for named production delivery channels requires real provider configuration, safe secret handling, provider error mapping, and local smoke or integration validation. Without provider choices, tasks may implement only neutral seams and fail-closed behavior.
- options:
  - A: Provider-neutral seams only for now; no production SMS/push/Slack/Teams adapters.
  - B: Implement webhook production delivery first; defer SMS/push/Slack/Teams provider choices.
  - C: Select concrete providers for each channel before implementing production adapters.
- default if deferred: A
- answer: none
- decision: pending
- decision impact: pending
- reconciled into:
  - none

### Q-002: Enterprise scope priority

- status: pending
- priority: important
- category: deployment
- depends on: []
- blocks:
  - detailed enterprise provider integration beyond foundation contracts
- source:
  - conversation identified enterprise IAM/SCIM/SSO, SIEM/legal hold/e-discovery, compliance suites, marketplace prompts, tenant-managed tool binding, and policy-as-code authoring as not implemented
- question: >
    Which enterprise extension should be implemented first after the provider-neutral foundations: IAM/SCIM/SSO admin, SIEM/legal hold/e-discovery, compliance reporting, marketplace prompts, tenant-managed tool binding, or policy-as-code?
- why it matters: >
    These are large platform areas. The mini-project can define bounded foundations, but detailed provider/product behavior should follow priority rather than broad speculative implementation.
- options:
  - A: IAM/SCIM/SSO administration first.
  - B: SIEM/legal hold/e-discovery first.
  - C: Compliance reporting first.
  - D: Marketplace prompts and tenant-managed tool binding first.
  - E: Policy-as-code first.
- default if deferred: A for foundation planning only; no provider-specific enterprise integration.
- answer: none
- decision: pending
- decision impact: pending
- reconciled into:
  - none
