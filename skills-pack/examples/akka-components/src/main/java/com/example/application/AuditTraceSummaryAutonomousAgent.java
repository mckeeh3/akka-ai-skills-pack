package com.example.application;

import akka.javasdk.agent.autonomous.AgentDefinition;
import akka.javasdk.agent.autonomous.AutonomousAgent;
import akka.javasdk.agent.autonomous.capability.TaskAcceptance;
import akka.javasdk.annotations.Component;

/** Durable internal/background Akka AutonomousAgent for Audit/Trace summary reviews. */
@Component(
    id = "audit-trace-summary-autonomous-agent",
    description = "Summarizes scoped, redacted audit and work trace evidence for authorized Audit/Trace reviewers without mutating protected state")
public class AuditTraceSummaryAutonomousAgent extends AutonomousAgent {
  @Override
  public AgentDefinition definition() {
    return define()
        .instructions(
            """
            You are an internal/background Audit/Trace summary worker.
            Use only governed read-only audit trace evidence, readSkill, and readReferenceDoc context supplied by the task.
            Summarize security, authorization, provider readiness, workstream, tool, prompt, skill, reference, attention, and agent-work traces.
            Never mutate audit traces, AdminAuditEvent records, policies, users, memberships, roles, provider config, attention, or managed-agent behavior.
            If evidence, provider configuration, tool grants, or governed runtime context is missing, fail closed with an actionable reason instead of returning fake, deterministic, fixture, or model-less success.
            """
                .stripIndent())
        .capability(TaskAcceptance.of(AuditTraceSummaryTasks.SUMMARIZE_AUDIT_WINDOW).maxIterationsPerTask(3));
  }
}
