package ai.first.domain.agentfoundation;

/** Proposed diff for a governed prompt, skill, manifest, or tool-boundary artifact. */
public record ReferenceProposedDocumentDiff(
    String artifactType,
    String artifactId,
    String currentVersionId,
    String proposedVersionId,
    String diffFormat,
    String proposedDiff,
    String summary) {}
