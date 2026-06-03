# Identity and Authorization

- principal-types:
  - employee
  - manager
  - internal-service
- identity-model:
  - all mutating actions require authenticated caller identity
- authorization-rules:
  - employee may create, edit, and submit only their own draft requests
  - manager may approve or reject submitted requests within their authorization scope
  - internal-service access is restricted to explicitly allowed operational surfaces only
- denial-behavior:
  - unauthorized mutation is denied
  - forbidden access must not expose protected request details beyond allowed error semantics
