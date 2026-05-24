# Workstream Akka Agent Runtime Migration

## Purpose

Close the remaining v0 implementation gap: user-facing workstream agents in the AI-first SaaS starter must execute through a real Akka `Agent` component and a real configured model provider boundary for normal runtime behavior.

The current starter has a governed service/provider path, but the selected workstream functional agent is not implemented as an Akka Agent component. That is not acceptable for a core feature described as fully implemented.

## Non-negotiable target

A v0 workstream message is implemented only when the local runtime path is:

1. frontend composer submits a prompt;
2. backend authenticates and resolves selected `AuthContext`;
3. backend authorizes the selected functional agent/capability;
4. governed runtime resolves active `AgentDefinition`, prompt, skill/reference manifests, tool boundary, model policy, and trace basis;
5. an Akka `Agent` component is invoked for the workstream response;
6. the Akka Agent uses configured model/provider settings for model-backed behavior;
7. missing/invalid provider configuration fails closed with actionable system-message/trace behavior;
8. user item, agent item, markdown surface, provider/model trace ids, and denial/error traces are durably recorded;
9. frontend renders the returned `markdown_response` surface;
10. tests and smoke validation prove the path without normal-runtime fake/canned responses.

Test doubles are allowed only in tests or explicitly named test adapters. They must not be the default generated-app runtime path.

## Scope

Primary files are under `templates/ai-first-saas-starter/`, with supporting source-repo regression tests and guidance updates as needed.

This queue supersedes any earlier task wording that allowed a deterministic local/demo model-response seam for normal workstream runtime behavior.
