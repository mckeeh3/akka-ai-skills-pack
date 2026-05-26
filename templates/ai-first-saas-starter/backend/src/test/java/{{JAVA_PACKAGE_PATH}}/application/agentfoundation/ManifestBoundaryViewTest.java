package {{JAVA_BASE_PACKAGE}}.application.agentfoundation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentLifecycleStatus;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.ToolPermissionBoundary;
import java.util.List;
import org.junit.jupiter.api.Test;

class ManifestBoundaryViewTest {
  @Test
  void manifestViewsProjectCompactRenderingInputsWithoutDocumentBodies() {
    var skillManifest = ManifestBoundaryEntityTest.skillManifest("tenant-1", "manifest-user-admin-skills", AgentLifecycleStatus.ACTIVE);
    var referenceManifest = ManifestBoundaryEntityTest.referenceManifest("tenant-1", "manifest-user-admin-references", AgentLifecycleStatus.ACTIVE);

    var skillRow = AgentSkillManifestView.SkillManifestRow.from(skillManifest);
    var referenceRow = AgentReferenceManifestView.ReferenceManifestRow.from(referenceManifest);

    assertEquals("tenant-1", skillRow.tenantId());
    assertEquals("agent-user-admin", skillRow.agentDefinitionId());
    assertEquals("ACTIVE", skillRow.lifecycleStatus());
    assertEquals(1, skillRow.entryCount());
    assertEquals("ua.access-review-triage.v1", skillRow.compactSkillEntries().get(0).stableSkillId());
    assertEquals("Use for access review requests.", skillRow.compactSkillEntries().get(0).whenToUse());
    assertFalse(skillRow.toString().contains("Before recommending access changes"));

    assertEquals("bundle-user-admin", referenceRow.workstreamExpertBundleId());
    assertEquals("ua.access-review-policy.v1", referenceRow.compactReferenceEntries().get(0).stableReferenceId());
    assertEquals("consult", referenceRow.compactReferenceEntries().get(0).allowedUse());
    assertEquals("internal", referenceRow.compactReferenceEntries().get(0).accessLevel());
    assertFalse(referenceRow.toString().contains("full reference body"));
  }

  @Test
  void toolBoundaryViewProjectsGrantSearchFlagsAndSeparateLoaderCategories() {
    var boundary = ManifestBoundaryEntityTest.boundary(
        "tenant-1",
        "boundary-user-admin",
        List.of(ManifestBoundaryEntityTest.readSkillGrant(), ManifestBoundaryEntityTest.readReferenceGrant()),
        AgentLifecycleStatus.ACTIVE);

    var row = ToolBoundaryGrantView.ToolBoundaryRow.from(boundary);

    assertEquals("tenant-1", row.tenantId());
    assertEquals("agent-user-admin", row.agentDefinitionId());
    assertEquals("ACTIVE", row.lifecycleStatus());
    assertEquals(2, row.grantCount());
    assertTrue(row.grantsReadSkill());
    assertTrue(row.grantsReadReference());
    assertTrue(row.grants().stream().anyMatch(grant -> grant.category().equals("read_skill") && grant.toolId().equals("readSkill")));
    assertTrue(row.grants().stream().anyMatch(grant -> grant.category().equals("read_reference") && grant.toolId().equals("readReferenceDoc")));
  }

  @Test
  void missingReferenceGrantIsVisibleToAgentAdminInspection() {
    var boundary = ManifestBoundaryEntityTest.boundary(
        "tenant-1",
        "boundary-user-admin",
        List.of(ManifestBoundaryEntityTest.readSkillGrant()),
        AgentLifecycleStatus.ACTIVE);

    var row = ToolBoundaryGrantView.ToolBoundaryRow.from(boundary);

    assertTrue(row.grantsReadSkill());
    assertFalse(row.grantsReadReference());
    assertEquals("read_skill", row.grants().get(0).category());
  }

  @Test
  void approvalRequiredGrantFlagSurfacesAuthorityExpansionReviewState() {
    var approvalRequiredGrant = new ToolPermissionBoundary.ToolGrant(
        "membership.role.change",
        ToolPermissionBoundary.Category.COMPONENT,
        "tenant.membership.role_change",
        List.of("request_approval"),
        List.of("runtime"),
        "security",
        "approval_required",
        true,
        "full_work_trace");
    var boundary = ManifestBoundaryEntityTest.boundary(
        "tenant-1",
        "boundary-user-admin",
        List.of(approvalRequiredGrant),
        AgentLifecycleStatus.ACTIVE);

    var row = ToolBoundaryGrantView.ToolBoundaryRow.from(boundary);

    assertTrue(row.containsApprovalRequiredGrant());
    assertEquals("security", row.grants().get(0).sideEffectLevel());
    assertEquals("approval_required", row.grants().get(0).autonomy());
  }
}
