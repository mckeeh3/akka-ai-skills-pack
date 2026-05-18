package com.example.application.agentfoundation;

import com.example.domain.agentfoundation.ReferenceAgentWorkTrace;
import com.example.domain.agentfoundation.ReferencePromptAssemblyTrace;
import com.example.domain.agentfoundation.ReferenceSkillLoadTrace;
import java.util.ArrayList;
import java.util.List;

/** In-memory trace sink for executable governed-agent reference tests. */
public final class ReferenceTraceSink {
  private final List<ReferencePromptAssemblyTrace> promptAssemblyTraces = new ArrayList<>();
  private final List<ReferenceSkillLoadTrace> skillLoadTraces = new ArrayList<>();
  private final List<ReferenceAgentWorkTrace> agentWorkTraces = new ArrayList<>();

  public void recordPromptAssembly(ReferencePromptAssemblyTrace trace) {
    promptAssemblyTraces.add(trace);
  }

  public void recordSkillLoad(ReferenceSkillLoadTrace trace) {
    skillLoadTraces.add(trace);
  }

  public void recordAgentWork(ReferenceAgentWorkTrace trace) {
    agentWorkTraces.add(trace);
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
}
