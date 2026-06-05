package ai.first.application;

import akka.javasdk.agent.autonomous.AgentDefinition;
import akka.javasdk.agent.autonomous.AutonomousAgent;
import akka.javasdk.agent.autonomous.capability.TaskAcceptance;
import akka.javasdk.annotations.Component;

/**
 * Governed Autonomous Agent reference example for backend-enforced local function-tool boundaries.
 */
@Component(
    id = "governed-risk-review-autonomous-agent",
    description =
        "Investigates customer risk as a durable background task while using only backend-governed tools.")
public class GovernedRiskReviewAutonomousAgent extends AutonomousAgent {

  @Override
  public AgentDefinition definition() {
    return define()
        .instructions(
            """
            Investigate the governed customer-risk review in the task instructions.
            Use GovernedRiskReviewTools_readCustomerEvidence for scoped evidence reads.
            Use GovernedRiskReviewTools_proposeCustomerFollowup only to request approval for a
            side-effecting follow-up; it must return approval_required before any side effect.
            Treat denied tool results as authoritative and do not invent or leak evidence.
            Complete GovernedRiskReview with tenantId, customerId, recommendation, evidenceIds,
            and proposedActionId when the governed work is done. If required security, provider,
            or ToolPermissionBoundary configuration is missing, fail the task closed with an
            actionable reason instead of producing a canned result.
            """
                .stripIndent())
        .tools(new GovernedRiskReviewTools())
        .capability(TaskAcceptance.of(GovernedRiskReviewTasks.REVIEW).maxIterationsPerTask(5));
  }
}
