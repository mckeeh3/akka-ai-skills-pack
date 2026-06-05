package ai.first.domain.foundation.invitation;

/** Invitation lifecycle states exposed through browser-safe admin DTOs. */
public enum InvitationStatus {
  PENDING_DELIVERY,
  SENT,
  DELIVERY_FAILED,
  ACCEPTED,
  EXPIRED,
  REVOKED
}
