package ai.first.application.agentfoundation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ai.first.domain.foundation.agent.AgentLifecycleStatus;
import ai.first.domain.foundation.agent.PromptDocument;
import ai.first.domain.foundation.agent.ReferenceDocument;
import ai.first.domain.foundation.agent.SkillDocument;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class GovernedDocumentViewTest {
  @Test
  void documentViewsProjectCatalogAndRuntimeLookupShapeWithoutContentBodies() {
    var now = Instant.parse("2026-05-20T00:00:00Z");
    var prompt = new PromptDocument("tenant-1", "prompt-user-admin", "agent-user-admin", "User Admin prompt", "system", AgentLifecycleStatus.ACTIVE, 1, "prompt body", "prompt-checksum", "seed", null, now, now);
    var skill = new SkillDocument("tenant-1", "skill-access-review", "access-review", "Access Review", "Review access risk.", "Use for access review.", List.of("foundation"), AgentLifecycleStatus.ACTIVE, 2, "skill body", "skill-checksum", null, now, now);
    var reference = new ReferenceDocument("tenant-1", "ref-access-review", "access-review-policy", "Access Review Policy", "Policy summary.", "Consult for access review.", ReferenceDocument.ReferenceType.POLICY, "internal", List.of("foundation"), AgentLifecycleStatus.ACTIVE, 3, "reference body", "reference-checksum", null, now, now);

    var promptRow = PromptDocumentView.PromptDocumentRow.from(prompt);
    var skillRow = SkillDocumentView.SkillDocumentRow.from(skill);
    var referenceRow = ReferenceDocumentView.ReferenceDocumentRow.from(reference);

    assertEquals("agent-user-admin", promptRow.agentDefinitionId());
    assertEquals(1, promptRow.activeVersion());
    assertEquals("ACTIVE", promptRow.lifecycleStatus());
    assertEquals("access-review", skillRow.stableSkillId());
    assertEquals(2, skillRow.activeVersion());
    assertEquals("access-review-policy", referenceRow.stableReferenceId());
    assertEquals("POLICY", referenceRow.referenceType());
    assertEquals(3, referenceRow.activeVersion());
  }
}
