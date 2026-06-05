# Acceptance Verification: Purchase Request

- scenario-1:
  - given an employee has a valid draft request
  - when the employee submits it
  - then the request enters submitted state
- scenario-2:
  - given a submitted request requiring approval
  - when an authorized manager approves it
  - then the request enters approved state and becomes immutable
- scenario-3:
  - given a submitted request requiring approval
  - when an authorized manager rejects it
  - then the request enters rejected state and becomes immutable
