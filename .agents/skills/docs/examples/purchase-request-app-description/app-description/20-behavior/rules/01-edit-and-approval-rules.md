# Rule Family: Edit and Approval Rules

- draft-edit-rule:
  - employees may edit their own requests only while the request is in draft state
- submission-rule:
  - submission requires item description, amount, and requester identity
- approval-rule:
  - only authorized managers may approve or reject requests that require approval
- terminal-lock-rule:
  - approved or rejected requests are immutable
- forbidden-behavior:
  - cross-user mutation of another employee's draft
  - approval by a non-manager
  - editing a terminal request
