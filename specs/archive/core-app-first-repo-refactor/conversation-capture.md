# Conversation Capture: Core App First Repository Refactor

## Decisions already discussed

- The current dual role of the repo is causing friction: it is both the skills-pack source project and the place where the core app is developed.
- The existing `templates/ai-first-saas-starter/` full-app template creates synchronization problems with root `src/` and `frontend/`.
- Custom Java package rendering is no longer required if users fork the core app rather than scaffold a brand-new app.
- The preferred direction is to make the repository itself the runnable core app with a standard Akka Java/Maven + frontend layout.
- Everything related to skills-pack development and maintenance should move under one top-level `skills-pack/` directory.
- App specs and app documentation should live at the top level because they are part of the core app and later domain-specific extensions.
- Downstream teams should fork the repo and add domain-specific extensions; this enables upstream core app changes to be merged into domain implementations through normal Git workflows.
- The full-app template is no longer needed under the fork-and-extend model.
- Focused Akka source examples used by the skills pack should remain available, but in an internal/reference directory under `skills-pack/`, not mixed with the runnable core app source.

## Accepted constraints

- The top-level layout should be familiar to real users of an Akka Java + frontend app.
- Domain-specific changes should be additive and isolated where possible so upstream core changes remain mergeable.
- The core app runtime completion doctrine still applies: do not claim runtime features work unless the local Akka/API/UI path works at the stated scope.
- Skills-pack users should not need to understand old template/scaffold internals.
- The refactor should be planned as a multi-session queue with one bounded task per fresh harness context.

## Rejected or de-emphasized alternatives

- Keeping both root app source and full-app template source as maintained copies.
- Continuing to require Java package placeholder rendering for scaffolded apps.
- Treating the root `frontend/` as a second maintained copy of the template frontend.
- Making the skills-pack source layout the primary shape users see at the repository root.

## Risks

- Broad path moves can break skills, docs, install scripts, validation tools, and existing specs.
- Existing pending task queues may refer to old paths; active queues need either migration or compatibility notes.
- If domain extension boundaries are not explicit, downstream forks may edit core internals and make upstream merges difficult.
- Removing the scaffold changes product positioning from scaffold-generation to fork-and-extend; docs and install guidance must be consistent.

## Unresolved questions

No blocking product decision is currently needed to start the planning/inventory work. Later tasks may discover specific packaging or compatibility decisions and should add pending questions if they would otherwise require guessing.
