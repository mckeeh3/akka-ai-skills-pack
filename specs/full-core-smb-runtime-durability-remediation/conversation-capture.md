# Conversation Capture

## Source discussion

After full-core SMB polish/release readiness completed, the user asked: "there are no in memory mockups, correct?"

A source scan found that the starter still contains in-memory runtime repositories/defaults and frontend fixture/demo paths. The assistant clarified that model-backed agent behavior does not complete through in-memory/mock/model-less normal responses, but the stronger claim "no in-memory mockups/default stores in the normal generated runtime" is not currently true.

The user agreed to create a remediation mini-project.

## Accepted decisions

- Do not cut the release under the stronger no-in-memory-normal-runtime bar until this remediation is complete.
- Create `specs/full-core-smb-runtime-durability-remediation/`.
- Start by inventorying and classifying all in-memory/mock/fixture/demo/canned/model-less paths.
- Replace, durably implement, or explicitly gate normal runtime in-memory defaults.
- Test-only fakes and fixtures may remain if clearly scoped to tests.
- Release handoff docs must be corrected if they currently recommend shipping before remediation.

## Risks

- Some in-memory repositories may be local scaffold defaults that need durable Akka Entity replacements, which may require several bounded tasks.
- Frontend fixture paths may be useful for visual inspection but must not be presented as normal runtime.
- Stale generated static assets may contain old fixture code; classify carefully before editing.
- Remediation must not weaken provider fail-closed behavior or introduce deterministic/model-less normal agent success.
