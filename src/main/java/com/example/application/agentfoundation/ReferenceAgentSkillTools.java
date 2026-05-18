package com.example.application.agentfoundation;

import com.example.domain.agentfoundation.ReferenceResolvedAgentRuntime;

/** Minimal wrapper exposing the governed readSkill(skillId) tool shape for reference tests. */
public final class ReferenceAgentSkillTools {
  private final ReferenceResolvedAgentRuntime runtime;
  private final ReferenceSkillReadAuthorizer authorizer;

  public ReferenceAgentSkillTools(
      ReferenceResolvedAgentRuntime runtime, ReferenceSkillReadAuthorizer authorizer) {
    this.runtime = runtime;
    this.authorizer = authorizer;
  }

  public String readSkill(String skillId) {
    var result = authorizer.readSkill(runtime, skillId);
    if (!result.allowed()) {
      return result.content();
    }
    return "Skill "
        + result.skillId()
        + "@"
        + result.skillVersionId()
        + "\nAuthority: guidance only; backend authorization and ToolPermissionBoundary still apply.\n"
        + result.content();
  }
}
