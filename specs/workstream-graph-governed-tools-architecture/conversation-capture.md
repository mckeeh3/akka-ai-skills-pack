# Conversation Capture: Workstream Graph and Governed-Tools Architecture

## User decisions

- The workstream model is not only UX/UI. It is the core decomposition and implementation architecture for the skills pack.
- Requirements ingestion is a series of component decomposition steps that starts with deciding whether requested functionality fits one workstream or multiple workstreams.
- Within each workstream, the next critical step is the dashboard: what is shown to humans as requiring attention and work.
- A dashboard is a specific kind of surface. Its primary objective is to show what requires attention, not generic analytics.
- Dashboard behavior is role-driven. Dashboards are role-specific. Drop the previously discussed `mode` concept for now.
- The dashboard is the trunk of the human work tree. Branches are surfaces. Surface actions and transitions form a surface graph.
- Surfaces can include familiar UI patterns such as lists, search/filter tables, graphs, forms, buttons, links, detail/admin/edit views, decision cards, and system messages, but they are workstream graph nodes rather than page-first screens.
- Example: User Admin dashboard shows two expired invitations; clicking opens a pending invitations list surface; clicking an invitation opens invitation admin/edit; the user cancels or resends. Those actions then decompose into backend Akka components.
- Internal agents should be modeled as workstream workers. Each workstream has an internal virtual dashboard agent that looks at what requires attention, delegates specific work to internal worker agents, and leaves only necessary work to human users.
- The goal is to do as much workstream-related work as safely possible using internal agent workers, escalating to humans as needed.
- The backend app can be understood as a collection of qualified tools. Humans are also actors that use tools through browser surfaces.
- Because bare `tool` can be confused with Akka/LLM tools, always qualify the term: `governed-tool`, `agent-tool`, `browser-tool`, `internal-tool`, etc.
- A high-level governed-tool can represent a capability; governed-tools can use other governed-tools. Capabilities remain product-level groupings.
- Governed-tools should live inside existing capability files and surface/action maps for now; do not create a new top-level app-description layer.
- Each workstream has one or more workstream skills/expertise documents describing the role-based workstream and each surface. These are used by workstream agents to handle chat requests and help users understand what they can do.
- Requirements ingestion must handle incremental inputs for existing apps, not just one-shot PRD decomposition.
- This needs to be baked into the pack now as the way the pack does things.

## Accepted constraints

- Do not implement the architecture as a separate optional side doctrine that future agents can ignore.
- Integrate it into existing core docs, app-description skills, PRD/spec/backlog flows, examples, and implementation routing.
- Preserve current secure AI-first SaaS foundation, managed-agent governance, authorization, audit, trace, and runtime validation requirements.
- Avoid ambiguous unqualified `tool` terminology where it could be confused with Akka agent tools.
- Keep governed-tools inside capability files and surface/action maps for now.

## Risks

- The current pack uses `capability` as a central term. The migration must avoid breaking useful capability-first semantics while adding governed-tools as executable units.
- Existing docs and skills use bare `tool` heavily for actual Akka agent tools; edits should qualify architecture-level usage without making SDK-specific guidance awkward.
- Surface graph and workstream agent graph concepts must not become optional polish; they must appear in primary intake/planning output contracts.
- Incremental-change handling must reconcile against existing graphs rather than generating parallel fresh workstream designs.

## Unresolved questions

None blocking. The exact wording of individual docs/skills can be refined during implementation tasks.
