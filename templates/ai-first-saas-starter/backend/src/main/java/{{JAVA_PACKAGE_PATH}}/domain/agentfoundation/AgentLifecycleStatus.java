package {{JAVA_BASE_PACKAGE}}.domain.agentfoundation;

/** Lifecycle for governed agent behavior artifacts. */
public enum AgentLifecycleStatus {
  DRAFT,
  IN_REVIEW,
  APPROVED,
  ACTIVE,
  DISABLED,
  DEPRECATED,
  ARCHIVED
}
