# Data Protection

- sensitive-fields:
  - requester identity
  - approval identity
  - any free-text field that may contain sensitive business detail
- protection-rules:
  - sensitive fields must not be exposed to unrelated users
  - logs and audit records must avoid raw secret or credential disclosure
  - review and query surfaces must respect role and ownership boundaries
