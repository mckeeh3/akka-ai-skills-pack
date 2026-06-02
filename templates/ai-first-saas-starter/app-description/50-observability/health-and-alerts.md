# Health and Alerts

- health signals:
  - backend process readiness
  - critical projection/view lag where observable
  - failed timed action batches
  - workflow error/dead-letter style conditions where observable
- alert-worthy conditions:
  - repeated authorization failures by actor/source
  - stuck high-priority goals
  - stale approval queues beyond threshold
  - agent tool failure spikes
  - projection lag affecting command center accuracy
