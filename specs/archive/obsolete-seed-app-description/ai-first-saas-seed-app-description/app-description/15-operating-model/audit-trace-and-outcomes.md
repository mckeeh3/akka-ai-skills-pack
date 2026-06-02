# Audit Trace and Outcomes

- trace categories:
  - work trace
  - decision trace
  - policy invocation
  - tool invocation
  - data access event
  - approval event
  - outcome link
- minimum audit fields:
  - tenant id, actor id or agent id, action, target object, timestamp, authorization basis, policy/version references, correlation id
- outcome loop:
  - completed goals can record outcome metrics, human feedback, accepted/rejected recommendations, and later business result links
- replay/simulation:
  - policy, prompt, threshold, or authority changes with material impact should define replay or simulation expectations before activation
