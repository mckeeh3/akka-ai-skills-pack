package {{JAVA_BASE_PACKAGE}}.domain.security;

/** Starter v1 reasons why a workstream needs human attention. */
public enum AttentionCategory {
  INVITATION_DELIVERY,
  PROVIDER_READINESS,
  GOVERNANCE_APPROVAL,
  AUDIT_FAILURE_EVIDENCE,
  ACCESS_REVIEW,
  POLICY_EXCEPTION,
  WORKFLOW_BLOCKED,
  AGENT_TASK_FAILED,
  SECURITY_REVIEW
}
