package ai.first.application.coreapp.useradmin;

import akka.javasdk.agent.autonomous.AutonomousAgent;
import akka.javasdk.agent.autonomous.AgentDefinition;
import akka.javasdk.agent.autonomous.capability.TaskAcceptance;
import akka.javasdk.annotations.Component;

/** Durable internal/background Akka AutonomousAgent for User Admin access-review investigations. */
@Component(
    id = "user-admin-access-review-autonomous-agent",
    description = "Investigates tenant/customer User Admin access evidence and produces advisory recommendations that require human review before any access change")
public final class UserAdminAccessReviewAutonomousAgent extends AutonomousAgent {
  @Override
  public AgentDefinition definition() {
    return define()
        .instructions("""
            You are an internal/background User Admin access-review worker.
            Use only governed read-only evidence in the task instructions.
            Never claim that you changed accounts, invitations, memberships, roles, capabilities, tenants, customers, provider config, or audit policy.
            If required evidence, provider configuration, or governed runtime context is missing, fail closed with an actionable reason instead of returning fabricated success.
            """)
        .capability(TaskAcceptance.of(UserAdminAccessReviewTasks.ACCESS_REVIEW).maxIterationsPerTask(3));
  }
}
