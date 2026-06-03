# Logs, Metrics, Traces, and Alerts

- logs-and-audit:
  - log request submission outcome
  - log approval or rejection outcome
  - emit auditable decision records for manager actions
- metrics:
  - count submitted requests
  - count approved requests
  - count rejected requests
  - count invalid submission attempts
- traces-and-correlation:
  - preserve request correlation by purchase-request id across submission and approval path
- alert-worthy-conditions:
  - repeated approval failures above normal threshold
  - unusual spike in invalid submissions
- diagnosability:
  - operators must be able to determine whether a request failed validation, awaits approval, or reached a terminal state
