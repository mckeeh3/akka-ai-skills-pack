package ai.first.application;

import akka.javasdk.agent.autonomous.AgentDefinition;
import akka.javasdk.agent.autonomous.AutonomousAgent;
import akka.javasdk.agent.autonomous.capability.TaskAcceptance;
import akka.javasdk.annotations.Component;

/**
 * Autonomous Agent triage example that transfers same-task ownership to a focused specialist.
 *
 * <p>Generated SaaS apps must guard higher-authority or broader-scope handoff targets with backend
 * policy, approval, authorization, and trace checks before the task is started or assigned. This
 * reference keeps triage and specialist at the same authority level to focus on Akka handoff
 * mechanics.
 */
@Component(
    id = "support-triage-autonomous-agent",
    description = "Classifies support requests and hands off billing issues to a billing specialist.")
public class SupportTriageAutonomousAgent extends AutonomousAgent {

  @Override
  public AgentDefinition definition() {
    return define()
        .instructions(
            "Classify the support request. If it is a billing issue, hand off the same task to the billing specialist with concise context; do not complete billing resolutions in triage.")
        .capability(
            TaskAcceptance.of(HandoffTriageTasks.RESOLVE)
                .maxIterationsPerTask(3)
                .canHandoffTo(BillingSupportSpecialistAutonomousAgent.class));
  }
}
