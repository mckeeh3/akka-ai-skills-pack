# Agent Roles and Authority

- coordinator agent:
  - responsibility: turn a goal into a draft plan, identify needed tools/data, assign specialist steps, summarize progress
  - authority: draft only until launch; may execute low-risk steps after plan approval if permissions allow
- specialist agent:
  - responsibility: perform bounded analysis, classification, summarization, recommendation, or tool-backed task
  - authority: limited to assigned task permissions
- evaluator/guardrail agent:
  - responsibility: evaluate outputs against policy, confidence, risk, completeness, and evidence sufficiency
  - authority: recommend pass/fail/escalate; cannot override human approvals
- non-responsibilities:
  - agents do not create users, change tenant roles, alter tenant isolation, or commit expanded policy authority autonomously
- required traces:
  - prompt/skill/policy version used
  - tool/data access requested and granted
  - recommendation, confidence, risk, evidence, and escalation reason
