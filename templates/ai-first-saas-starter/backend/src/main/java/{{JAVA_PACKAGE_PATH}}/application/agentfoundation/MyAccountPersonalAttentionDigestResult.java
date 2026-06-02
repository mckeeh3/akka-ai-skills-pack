package {{JAVA_BASE_PACKAGE}}.application.agentfoundation;

import java.util.List;

/** Typed advisory result returned by the My Account personal attention digest Akka AutonomousAgent task. */
public record MyAccountPersonalAttentionDigestResult(
    String digestTaskId,
    String tenantId,
    String customerId,
    String accountId,
    String selectedContextId,
    String summary,
    int authorizedAttentionCount,
    DigestUrgency highestUrgency,
    List<PersonalAttentionDigestSection> sections,
    List<PersonalAttentionDigestRecommendation> recommendations,
    List<String> evidenceRefs,
    List<String> traceIds,
    String safety) {
  public MyAccountPersonalAttentionDigestResult {
    sections = List.copyOf(sections == null ? List.of() : sections);
    recommendations = List.copyOf(recommendations == null ? List.of() : recommendations);
    evidenceRefs = List.copyOf(evidenceRefs == null ? List.of() : evidenceRefs);
    traceIds = List.copyOf(traceIds == null ? List.of() : traceIds);
  }

  public enum DigestUrgency { NONE, INFO, WARNING, URGENT, BLOCKED }

  public record PersonalAttentionDigestSection(
      String sectionId,
      String sourceWorkstreamId,
      String heading,
      String redactedSummary,
      String highestSeverity,
      int authorizedItemCount,
      List<String> evidenceRefs) {
    public PersonalAttentionDigestSection {
      evidenceRefs = List.copyOf(evidenceRefs == null ? List.of() : evidenceRefs);
    }
  }

  public record PersonalAttentionDigestRecommendation(
      String recommendationId,
      String label,
      String rationale,
      String targetFunctionalAgentId,
      String targetSurfaceId,
      String requiredCapabilityId,
      List<String> evidenceRefs) {
    public PersonalAttentionDigestRecommendation {
      evidenceRefs = List.copyOf(evidenceRefs == null ? List.of() : evidenceRefs);
    }
  }
}
