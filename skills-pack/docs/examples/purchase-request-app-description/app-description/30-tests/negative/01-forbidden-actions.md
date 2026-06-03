# Negative Verification: Forbidden Actions

- unauthorized-approval:
  - given a caller without manager authority
  - when they attempt to approve a submitted request
  - then the action is denied
- incomplete-submission:
  - given a draft request is missing required fields
  - when submission is attempted
  - then the request is rejected and remains non-submitted
- cross-user-edit:
  - given an employee does not own the draft request
  - when they attempt to edit it
  - then the action is denied
