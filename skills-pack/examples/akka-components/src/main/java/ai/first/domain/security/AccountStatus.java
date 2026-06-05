package ai.first.domain.security;

/** Local account status used by backend authorization. */
public enum AccountStatus {
  INVITED,
  ACTIVE,
  DISABLED,
  DELETED
}
