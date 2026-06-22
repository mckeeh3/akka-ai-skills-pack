# Sprint 03: Surface Catalogs and All-Workstream Expansion

## Goal

Add surface familiarity metadata and expand deterministic routing across all five core workstreams.

## Workstreams

- My Account
- User Admin
- Agent Admin
- Audit/Trace
- Governance/Policy

## Required outcomes

- Each workstream has a small catalog of surfaces, prompt examples, required capabilities, prefill fields, and forbidden direct effects.
- Router support extends to high-confidence surface-open/prefill intents for each workstream.
- Catalog metadata is available to backend routing and/or governed agent familiarity material without granting command authority.

## Validation focus

- contract tests for representative prompt-to-surface matches per workstream;
- denials or fallback for ambiguous/high-risk prompts;
- no direct command submission.
