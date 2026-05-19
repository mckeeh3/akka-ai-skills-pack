# Identity and Trust

- authentication model:
  - all browser users authenticate with WorkOS/AuthKit before accessing app APIs
  - WorkOS is the supported production user auth service for this seed
  - local development may use controlled WorkOS-compatible test tokens only when clearly isolated from production
- trust boundaries:
  - browser is untrusted
  - backend endpoints enforce identity, tenant scope, permissions, and policy gates
  - agent outputs are untrusted until validated and authorized
- identity claims:
  - stable user id
  - email/display fields as non-authoritative profile data
  - active tenant selected by request/context, not blindly trusted from client claims
