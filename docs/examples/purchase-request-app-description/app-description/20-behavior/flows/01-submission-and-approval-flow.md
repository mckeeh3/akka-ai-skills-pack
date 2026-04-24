# Flow: Submission and Approval

- trigger:
  - employee submits a valid draft purchase request
- happy-path:
  - system validates required fields
  - system records the request as submitted
  - if approval is required, manager may approve or reject
  - if approved, request becomes terminal approved state
  - if rejected, request becomes terminal rejected state
- approval-threshold-rule:
  - requests at or above the configured threshold require manager approval
  - requests below the threshold may be auto-approved or treated as approved without a manual manager action, depending on later policy extension
- failure-behavior:
  - invalid draft submission is rejected without creating a submitted state
  - approval action against non-submitted request is rejected or normalized to no-op according to lifecycle rule
- linked-rules:
  - `../rules/01-edit-and-approval-rules.md`
