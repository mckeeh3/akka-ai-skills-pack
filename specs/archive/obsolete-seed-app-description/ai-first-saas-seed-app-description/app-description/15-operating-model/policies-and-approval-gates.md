# Policies and Approval Gates

- policy objects:
  - policy document, policy clause, guardrail, threshold, permission, policy version, policy proposal, policy commit
- approval gates:
  - high-impact action gate
  - low-confidence recommendation gate
  - external side-effect gate
  - sensitive data access gate
  - policy authority expansion gate
- governed policy changes:
  - agent may draft a policy proposal with rationale and simulation/replay expectations
  - policy owner must approve before activation
  - all activated policy changes record provenance and version
- enforcement rule:
  - backend authorization and workflow steps enforce gates mechanically; prompts may explain gates but do not replace enforcement
