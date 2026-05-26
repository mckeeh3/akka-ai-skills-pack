package {{JAVA_BASE_PACKAGE}}.application.agentfoundation;

import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.ToolCatalogEntry;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.ToolPermissionBoundary;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/** Backend-owned registry that maps stable tool ids to hardcoded Java bindings. */
public final class ToolRegistry {
  public static final String READ_SKILL_TOOL_ID = "readSkill";
  public static final String READ_REFERENCE_DOC_TOOL_ID = "readReferenceDoc";
  static final String READ_SKILL_BINDING = "governed-loader.readSkill";
  static final String READ_REFERENCE_DOC_BINDING = "governed-loader.readReferenceDoc";

  private final Map<String, RegisteredTool> toolsByStableToolId;

  public ToolRegistry(List<RegisteredTool> tools) {
    var ordered = new LinkedHashMap<String, RegisteredTool>();
    for (var tool : tools == null ? List.<RegisteredTool>of() : tools) {
      ordered.put(tool.entry().toolId(), tool);
    }
    this.toolsByStableToolId = Map.copyOf(ordered);
  }

  public static ToolRegistry starterDefault() {
    return new ToolRegistry(List.of(
        new RegisteredTool(
            new ToolCatalogEntry(
                READ_SKILL_TOOL_ID,
                "Read governed skill",
                ToolPermissionBoundary.Category.READ_SKILL,
                "agent.skills.read",
                "Loads assigned procedural guidance through governed readSkill(skillId) authorization.",
                ToolCatalogEntry.SideEffectLevel.NONE,
                READ_SKILL_BINDING),
            context -> new RuntimeToolBinding(READ_SKILL_TOOL_ID, READ_SKILL_BINDING, context.tenantId(), context.agentDefinitionId(), context.mode(), context.correlationId())),
        new RegisteredTool(
            new ToolCatalogEntry(
                READ_REFERENCE_DOC_TOOL_ID,
                "Read governed reference document",
                ToolPermissionBoundary.Category.READ_REFERENCE,
                "agent.references.read",
                "Loads assigned factual/process reference evidence through governed readReferenceDoc(referenceId) authorization.",
                ToolCatalogEntry.SideEffectLevel.NONE,
                READ_REFERENCE_DOC_BINDING),
            context -> new RuntimeToolBinding(READ_REFERENCE_DOC_TOOL_ID, READ_REFERENCE_DOC_BINDING, context.tenantId(), context.agentDefinitionId(), context.mode(), context.correlationId()))));
  }

  public Optional<RegisteredTool> find(String toolId) {
    return Optional.ofNullable(toolsByStableToolId.get(toolId));
  }

  public List<ToolCatalogEntry> entries() {
    return toolsByStableToolId.values().stream().map(RegisteredTool::entry).toList();
  }

  public List<String> stableToolIds() {
    return new ArrayList<>(toolsByStableToolId.keySet());
  }

  public record RegisteredTool(ToolCatalogEntry entry, Function<BindingContext, Object> bindingFactory) {
    public Object createBinding(BindingContext context) {
      return bindingFactory.apply(context);
    }
  }

  public record BindingContext(String tenantId, String agentDefinitionId, String mode, String correlationId) {}

  /**
   * Placeholder backend-owned binding marker for starter task 01-001.
   * Follow-on tasks replace these markers with request-scoped @FunctionTool objects.
   */
  public record RuntimeToolBinding(String toolId, String implementationBindingKey, String tenantId, String agentDefinitionId, String mode, String correlationId) {}
}
