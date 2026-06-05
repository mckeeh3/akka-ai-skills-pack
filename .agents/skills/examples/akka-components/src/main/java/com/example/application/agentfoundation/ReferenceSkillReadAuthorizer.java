package com.example.application.agentfoundation;

import com.example.domain.agentfoundation.ReferenceResolvedAgentRuntime;
import com.example.domain.agentfoundation.ReferenceSkillDocument;
import com.example.domain.agentfoundation.ReferenceSkillLoadTrace;
import com.example.domain.agentfoundation.ReferenceSkillVersion;
import java.util.Map;

/** Reference-only readSkill(skillId) authorizer that fails closed and traces every result. */
public final class ReferenceSkillReadAuthorizer {
  public static final String READ_SKILL_TOOL_ID = "readSkill";
  public static final String SAFE_DENIAL = "Skill unavailable for this agent invocation.";

  private final Map<String, ReferenceSkillDocument> skillDocuments;
  private final Map<String, ReferenceSkillVersion> skillVersions;
  private final ReferenceTraceSink traceSink;

  public ReferenceSkillReadAuthorizer(
      Map<String, ReferenceSkillDocument> skillDocuments,
      Map<String, ReferenceSkillVersion> skillVersions,
      ReferenceTraceSink traceSink) {
    this.skillDocuments = Map.copyOf(skillDocuments);
    this.skillVersions = Map.copyOf(skillVersions);
    this.traceSink = traceSink;
  }

  public ReferenceSkillReadResult readSkill(
      ReferenceResolvedAgentRuntime runtime, String requestedSkillId) {
    if (runtime == null || !runtime.allowed()) {
      return deny(runtime, requestedSkillId, "runtime denied", "");
    }
    if (!runtime.agentDefinition().activeForRuntime()) {
      return deny(runtime, requestedSkillId, "agent inactive", "");
    }
    if (runtime.skillManifest() == null || !runtime.skillManifest().active()) {
      return deny(runtime, requestedSkillId, "manifest inactive", "");
    }
    if (!runtime.skillManifest().assignsSkill(requestedSkillId)) {
      return deny(runtime, requestedSkillId, "skill not assigned", "");
    }
    if (runtime.toolPermissionBoundary() == null
        || !runtime
            .toolPermissionBoundary()
            .allowsTool(READ_SKILL_TOOL_ID, runtime.authContext().mode())) {
      return deny(runtime, requestedSkillId, "readSkill tool not granted", "");
    }

    var document = skillDocuments.get(requestedSkillId);
    if (document == null
        || !document.active()
        || !document.tenantId().equals(runtime.authContext().tenantId())) {
      return deny(runtime, requestedSkillId, "skill document unavailable", "");
    }

    var manifestVersionRef = runtime.skillManifest().activeSkillVersionFor(requestedSkillId);
    if (manifestVersionRef == null || !manifestVersionRef.equals(document.activeSkillVersionId())) {
      return deny(runtime, requestedSkillId, "manifest skill version denied", manifestVersionRef);
    }

    var version = skillVersions.get(manifestVersionRef);
    if (version == null
        || !version.tenantId().equals(runtime.authContext().tenantId())
        || !version.skillDocumentId().equals(document.skillDocumentId())
        || !version.activeForRuntime()) {
      return deny(runtime, requestedSkillId, "skill version unavailable", manifestVersionRef);
    }

    trace(runtime, requestedSkillId, version.skillVersionId(), true, "allowed");
    return new ReferenceSkillReadResult(
        true,
        "allowed",
        requestedSkillId,
        version.skillVersionId(),
        document.displayName(),
        version.content(),
        version.checksum());
  }

  private ReferenceSkillReadResult deny(
      ReferenceResolvedAgentRuntime runtime,
      String requestedSkillId,
      String reason,
      String resolvedSkillVersionId) {
    trace(runtime, requestedSkillId, resolvedSkillVersionId == null ? "" : resolvedSkillVersionId, false, reason);
    return ReferenceSkillReadResult.denied(reason, requestedSkillId);
  }

  private void trace(
      ReferenceResolvedAgentRuntime runtime,
      String requestedSkillId,
      String resolvedSkillVersionId,
      boolean allowed,
      String reason) {
    var tenantId = runtime == null || runtime.authContext() == null ? "" : runtime.authContext().tenantId();
    var agentDefinitionId = runtime == null || runtime.agentDefinition() == null ? "" : runtime.agentDefinition().agentDefinitionId();
    var manifestId = runtime == null || runtime.skillManifest() == null ? "" : runtime.skillManifest().skillManifestId();
    var boundaryId = runtime == null || runtime.toolPermissionBoundary() == null ? "" : runtime.toolPermissionBoundary().toolBoundaryId();
    var mode = runtime == null || runtime.authContext() == null ? "" : runtime.authContext().mode();
    var correlationId = runtime == null ? "" : runtime.correlationId();
    traceSink.recordSkillLoad(
        new ReferenceSkillLoadTrace(
            tenantId,
            agentDefinitionId,
            manifestId,
            requestedSkillId,
            resolvedSkillVersionId,
            boundaryId,
            mode,
            correlationId,
            allowed,
            reason));
  }

  public record ReferenceSkillReadResult(
      boolean allowed,
      String decisionReason,
      String skillId,
      String skillVersionId,
      String title,
      String content,
      String checksum) {

    public static ReferenceSkillReadResult denied(String reason, String skillId) {
      return new ReferenceSkillReadResult(false, reason, skillId, "", "", SAFE_DENIAL, "");
    }
  }
}
