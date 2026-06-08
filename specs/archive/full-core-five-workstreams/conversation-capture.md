# Conversation Capture: Full-Core Five Workstreams

## Trigger

The user asked what “explicit full-core/demo follow-up behavior” means after seeing it in core workstream surfaces. We identified it as a scope boundary: the five-core v0 bootstrap shows one minimal real `markdown_response` per core workstream, while richer surfaces/actions must be enabled through explicit follow-up/full-core behavior.

The user then asked how to proceed with full implementations of the five core workstreams and confirmed that the skills pack has been realigned around a stronger workstream/surface/agents focus that should be referenced as **THE WAY** app requirements are decomposed into fully functional secure AI-first SaaS apps.

## Accepted decisions

- Preserve the v0 bootstrap: do not make the initial app load a rich demo dashboard set.
- Full-core work is explicit follow-up work with named surfaces, actions, capabilities, agents, and validation.
- Planning and implementation must reference the current skills-pack doctrine, especially the requirements-to-workstream process.
- Workstreams, surfaces, functional agents, internal agent graph, governed capabilities/governed-tools, and Akka substrate form the decomposition chain.
- Full-core means real runtime behavior, not fixture-only or deterministic/demo behavior.

## Non-goals and rejected alternatives

- Do not silently reintroduce richer dashboard/list/detail/audit/governance surfaces into the v0 bootstrap acceptance path.
- Do not treat pages, CRUD screens, endpoint lists, or Akka components as the root decomposition.
- Do not count mock/demo/model-less behavior as implementation completion for named workstream features.

## Risks

- The full-core scope can become too broad if tasks are not vertical and bounded.
- Agent governance work can be collapsed into vague “agent admin” work unless tasks preserve AgentDefinition, prompt, skill, reference, manifest, tool-boundary, behavior-editing, and trace scopes.
- UI can drift into page-first admin consoles unless every surface/action maps back to a workstream and governed capability.

## Unresolved questions

No blocking questions for the planning scaffold. Implementation tasks may discover bounded questions about exact payload fields, UI layout, model policy defaults, or approval thresholds; those should be captured as blocked task notes or follow-up tasks rather than guessed.
