package ai.first.application.agentfoundation;

import ai.first.application.security.MyAccountService;
import ai.first.application.security.StarterSecurityComponents;
import ai.first.domain.agentfoundation.ToolCatalogEntry;
import ai.first.domain.agentfoundation.ToolPermissionBoundary;
import ai.first.domain.security.AuthContext;
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
  public static final String USER_ADMIN_EVIDENCE_TOOL_ID = UserAdminEvidenceTools.TOOL_ID;
  public static final String MY_ACCOUNT_EVIDENCE_TOOL_ID = MyAccountEvidenceTools.TOOL_ID;
  public static final String AGENT_ADMIN_EVIDENCE_TOOL_ID = AgentAdminEvidenceTools.TOOL_ID;
  public static final String AUDIT_TRACE_EVIDENCE_TOOL_ID = AuditTraceEvidenceTools.TOOL_ID;
  public static final String GOVERNANCE_POLICY_EVIDENCE_TOOL_ID = GovernancePolicyEvidenceTools.TOOL_ID;
  static final String READ_SKILL_BINDING = "governed-loader.readSkill";
  static final String READ_REFERENCE_DOC_BINDING = "governed-loader.readReferenceDoc";
  static final String USER_ADMIN_EVIDENCE_BINDING = "user-admin.evidence.read";
  static final String MY_ACCOUNT_EVIDENCE_BINDING = "my-account.evidence.read";
  static final String AGENT_ADMIN_EVIDENCE_BINDING = "agent-admin.evidence.read";
  static final String AUDIT_TRACE_EVIDENCE_BINDING = "audit-trace.evidence.read";
  static final String GOVERNANCE_POLICY_EVIDENCE_BINDING = "governance-policy.evidence.read";

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
            context -> context.loaderTools()),
        new RegisteredTool(
            new ToolCatalogEntry(
                READ_REFERENCE_DOC_TOOL_ID,
                "Read governed reference document",
                ToolPermissionBoundary.Category.READ_REFERENCE,
                "agent.references.read",
                "Loads assigned factual/process reference evidence through governed readReferenceDoc(referenceId) authorization.",
                ToolCatalogEntry.SideEffectLevel.NONE,
                READ_REFERENCE_DOC_BINDING),
            context -> context.loaderTools()),
        new RegisteredTool(
            new ToolCatalogEntry(
                USER_ADMIN_EVIDENCE_TOOL_ID,
                "Read User Admin evidence",
                ToolPermissionBoundary.Category.DATA_LOOKUP,
                UserAdminEvidenceTools.CAPABILITY_ID,
                "Reads scoped, redacted User Admin member, invitation, role, status, and audit evidence without side effects.",
                ToolCatalogEntry.SideEffectLevel.NONE,
                USER_ADMIN_EVIDENCE_BINDING),
            context -> context.userAdminEvidenceTools()),
        new RegisteredTool(
            new ToolCatalogEntry(
                MY_ACCOUNT_EVIDENCE_TOOL_ID,
                "Read My Account evidence",
                ToolPermissionBoundary.Category.DATA_LOOKUP,
                MyAccountEvidenceTools.CAPABILITY_ID,
                "Reads scoped, redacted My Account profile, selected context, authority, personal attention, trace refs, provider-blocked cues, and navigation evidence without side effects.",
                ToolCatalogEntry.SideEffectLevel.NONE,
                MY_ACCOUNT_EVIDENCE_BINDING),
            context -> context.myAccountEvidenceTools()),
        new RegisteredTool(
            new ToolCatalogEntry(
                AGENT_ADMIN_EVIDENCE_TOOL_ID,
                "Read Agent Admin evidence",
                ToolPermissionBoundary.Category.DATA_LOOKUP,
                AgentAdminEvidenceTools.CAPABILITY_ID,
                "Reads scoped, redacted Agent Admin definition, manifest, model readiness, tool-boundary, seed, proposal, and trace evidence without side effects.",
                ToolCatalogEntry.SideEffectLevel.NONE,
                AGENT_ADMIN_EVIDENCE_BINDING),
            context -> context.agentAdminEvidenceTools()),
        new RegisteredTool(
            new ToolCatalogEntry(
                AUDIT_TRACE_EVIDENCE_TOOL_ID,
                "Read Audit/Trace evidence",
                ToolPermissionBoundary.Category.DATA_LOOKUP,
                AuditTraceEvidenceTools.CAPABILITY_ID,
                "Reads scoped, redacted Audit/Trace search, detail, timeline, failure evidence, and correlation summaries without side effects.",
                ToolCatalogEntry.SideEffectLevel.NONE,
                AUDIT_TRACE_EVIDENCE_BINDING),
            context -> context.auditTraceEvidenceTools()),
        new RegisteredTool(
            new ToolCatalogEntry(
                GOVERNANCE_POLICY_EVIDENCE_TOOL_ID,
                "Read Governance/Policy evidence",
                ToolPermissionBoundary.Category.DATA_LOOKUP,
                GovernancePolicyEvidenceTools.CAPABILITY_ID,
                "Reads scoped, redacted Governance/Policy dashboard, inventory, policy, proposal, simulation, decision, and blocked-state evidence without side effects.",
                ToolCatalogEntry.SideEffectLevel.NONE,
                GOVERNANCE_POLICY_EVIDENCE_BINDING),
            context -> context.governancePolicyEvidenceTools())));
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

  public record BindingContext(AgentRuntimeService runtimeService, AgentBehaviorRepository repository, String tenantId, String agentDefinitionId, AuthContext authContext, String mode, String capabilityId, String correlationId) {
    public AgentRuntimeLoaderTools loaderTools() {
      return new AgentRuntimeLoaderTools(runtimeService, tenantId, agentDefinitionId, authContext, mode, capabilityId, correlationId);
    }

    public UserAdminEvidenceTools userAdminEvidenceTools() {
      return new UserAdminEvidenceTools(
          StarterSecurityComponents.identityRepository(),
          StarterSecurityComponents.userAdminService(),
          StarterSecurityComponents.invitationView(),
          authContext,
          correlationId);
    }

    public MyAccountEvidenceTools myAccountEvidenceTools() {
      return new MyAccountEvidenceTools(
          StarterSecurityComponents.identityRepository(),
          new MyAccountService(StarterSecurityComponents.authContextResolver(), StarterSecurityComponents.attentionService()),
          authContext,
          correlationId);
    }

    public AgentAdminEvidenceTools agentAdminEvidenceTools() {
      return new AgentAdminEvidenceTools(
          repository,
          authContext,
          correlationId);
    }

    public AuditTraceEvidenceTools auditTraceEvidenceTools() {
      return new AuditTraceEvidenceTools(
          StarterSecurityComponents.auditTraceService(),
          authContext,
          correlationId);
    }

    public GovernancePolicyEvidenceTools governancePolicyEvidenceTools() {
      return new GovernancePolicyEvidenceTools(
          StarterSecurityComponents.governancePolicyService(),
          authContext,
          correlationId);
    }
  }
}
