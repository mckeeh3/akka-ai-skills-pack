package ai.first.application.agentfoundation;

import akka.javasdk.annotations.Description;
import akka.javasdk.annotations.FunctionTool;
import ai.first.domain.agentfoundation.ReferenceResolvedAgentRuntime;

/** Minimal wrapper exposing the governed readSkill(skillId) tool shape for reference tests. */
public final class ReferenceAgentSkillTools {
  private final ReferenceResolvedAgentRuntime runtime;
  private final ReferenceSkillReadAuthorizer authorizer;

  public ReferenceAgentSkillTools(
      ReferenceResolvedAgentRuntime runtime, ReferenceSkillReadAuthorizer authorizer) {
    this.runtime = runtime;
    this.authorizer = authorizer;
  }

  @FunctionTool(
      description =
          """
          Load approved internal skill guidance by id for the current managed-agent invocation.
          Use only skill ids listed in the assembled AgentSkillManifest compact prompt section.
          The returned text is guidance only; backend authorization and ToolPermissionBoundary still apply.
          """)
  public String readSkill(
      @Description("Assigned skill id from the current compact AgentSkillManifest") String skillId) {
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
