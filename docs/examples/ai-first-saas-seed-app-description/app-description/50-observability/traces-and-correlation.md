# Traces and Correlation

- every user request and workflow execution has a correlation id
- workflow, entity command, consumer reaction, timed action, and agent invocation should preserve correlation where feasible
- UI error reports should expose safe request/correlation ids for support
- decision cards and audit traces link back to originating goal/plan/task/workflow ids
