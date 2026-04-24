# Operational Verification: Audit and Diagnosability

- audit-submission:
  - submission creates an auditable record with request id, requester, and outcome
- audit-approval-decision:
  - approval or rejection creates an auditable record with approver identity and decision outcome
- diagnosable-failure:
  - invalid submission failures remain visible through structured logs or equivalent operational evidence
- correlation:
  - submission and later approval events remain traceable by purchase-request id
