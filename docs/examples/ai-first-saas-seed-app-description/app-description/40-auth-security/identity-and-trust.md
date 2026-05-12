# Identity and Trust

- authentication model:
  - all browser users authenticate before accessing app APIs
  - implementation must preserve a seam for production identity providers
  - local development may use a controlled dev-auth adapter if clearly isolated
- trust boundaries:
  - browser is untrusted
  - backend endpoints enforce identity, tenant scope, permissions, and policy gates
  - agent outputs are untrusted until validated and authorized
- identity claims:
  - stable user id
  - email/display fields as non-authoritative profile data
  - active tenant selected by request/context, not blindly trusted from client claims
