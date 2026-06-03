# Regression Verification: Repeat Actions

- repeated-approval:
  - given a request is already approved
  - when the same approval action is replayed
  - then no duplicate approval side effect occurs
- repeated-submission-terminal:
  - given a request is already approved or rejected
  - when submission is attempted again
  - then the request does not re-enter submitted state
- draft-edit-after-terminal:
  - given a request is terminal
  - when the requester tries to edit it
  - then the edit is rejected and terminal state is preserved
