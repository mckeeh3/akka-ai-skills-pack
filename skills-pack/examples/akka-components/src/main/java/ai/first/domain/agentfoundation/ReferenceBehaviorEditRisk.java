package ai.first.domain.agentfoundation;

/** Risk classification for agent-mediated behavior edit proposals in reference examples. */
public enum ReferenceBehaviorEditRisk {
  LOW(false, false),
  MEDIUM(false, false),
  HIGH(true, true),
  BLOCKED(true, true);

  private final boolean decisionCardRequired;
  private final boolean authorityReviewRequired;

  ReferenceBehaviorEditRisk(boolean decisionCardRequired, boolean authorityReviewRequired) {
    this.decisionCardRequired = decisionCardRequired;
    this.authorityReviewRequired = authorityReviewRequired;
  }

  public boolean decisionCardRequired() {
    return decisionCardRequired;
  }

  public boolean authorityReviewRequired() {
    return authorityReviewRequired;
  }
}
