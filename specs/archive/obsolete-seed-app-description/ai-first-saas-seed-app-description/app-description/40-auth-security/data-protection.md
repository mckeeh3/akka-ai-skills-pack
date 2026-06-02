# Data Protection

- tenant data isolation is mandatory for writes, reads, projections, traces, and realtime streams
- sensitive fields must be redacted from logs and safe error messages
- audit traces may include references to sensitive data but should avoid unnecessary payload storage
- secrets are never stored in app-description or frontend code
- retention policy is configurable by tenant where supported in later phases
