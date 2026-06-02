package {{JAVA_BASE_PACKAGE}}.application.agentfoundation;

import akka.javasdk.agent.autonomous.AgentDefinition;
import akka.javasdk.agent.autonomous.AutonomousAgent;
import akka.javasdk.agent.autonomous.capability.TaskAcceptance;
import akka.javasdk.annotations.Component;

/** Durable internal/background Akka AutonomousAgent for My Account personal attention digests. */
@Component(
    id = "my-account-personal-attention-digest-autonomous-agent",
    description = "Summarizes authorized My Account personal attention evidence for the signed-in account without mutating source attention")
public final class MyAccountPersonalAttentionDigestAutonomousAgent extends AutonomousAgent {
  @Override
  public AgentDefinition definition() {
    return define()
        .instructions("""
            You are an internal/background My Account personal attention digest worker.
            Use only the backend-authorized personal attention evidence, readSkill, and readReferenceDoc context supplied by the task.
            Summarize only visible attention for the signed-in account and selected AuthContext; never infer hidden workstream, item, trace, count, or severity existence.
            Provide advisory navigation/review recommendations only. Never mutate source attention items, roles, memberships, policies, provider config, traces, or managed-agent behavior.
            If evidence, provider configuration, tool grants, or governed runtime context is missing, fail closed with an actionable reason instead of returning fake, deterministic, or model-less digest success.
            """)
        .capability(TaskAcceptance.of(MyAccountPersonalAttentionDigestTasks.PERSONAL_ATTENTION_DIGEST).maxIterationsPerTask(3));
  }
}
