package com.example.application.agentfoundation;

import com.example.domain.agentfoundation.ReferenceAgentWorkTrace;
import com.example.domain.agentfoundation.ReferenceBehaviorEditTrace;
import com.example.domain.agentfoundation.ReferenceImprovementTrace;
import com.example.domain.agentfoundation.ReferencePromptAssemblyTrace;
import com.example.domain.agentfoundation.ReferenceSkillLoadTrace;
import java.util.ArrayList;
import java.util.List;

/** In-memory trace sink for executable governed-agent reference tests. */
public final class ReferenceTraceSink {
  private final List<ReferencePromptAssemblyTrace> promptAssemblyTraces = new ArrayList<>();
  private final List<ReferenceSkillLoadTrace> skillLoadTraces = new ArrayList<>();
  private final List<ReferenceAgentWorkTrace> agentWorkTraces = new ArrayList<>();
  private final List<ReferenceBehaviorEditTrace> behaviorEditTraces = new ArrayList<>();
  private final List<ReferenceImprovementTrace> improvementTraces = new ArrayList<>();

  public void recordPromptAssembly(ReferencePromptAssemblyTrace trace) {
    promptAssemblyTraces.add(trace);
  }

  public void recordSkillLoad(ReferenceSkillLoadTrace trace) {
    skillLoadTraces.add(trace);
  }

  public void recordAgentWork(ReferenceAgentWorkTrace trace) {
    agentWorkTraces.add(trace);
  }

  public void recordBehaviorEdit(ReferenceBehaviorEditTrace trace) {
    behaviorEditTraces.add(trace);
  }

  public void recordImprovement(ReferenceImprovementTrace trace) {
    improvementTraces.add(trace);
  }

  public List<ReferencePromptAssemblyTrace> promptAssemblyTraces() {
    return List.copyOf(promptAssemblyTraces);
  }

  public List<ReferenceSkillLoadTrace> skillLoadTraces() {
    return List.copyOf(skillLoadTraces);
  }

  public List<ReferenceAgentWorkTrace> agentWorkTraces() {
    return List.copyOf(agentWorkTraces);
  }

  public List<ReferenceBehaviorEditTrace> behaviorEditTraces() {
    return List.copyOf(behaviorEditTraces);
  }

  public List<ReferenceImprovementTrace> improvementTraces() {
    return List.copyOf(improvementTraces);
  }
}
