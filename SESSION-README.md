# SESSION-README

Use this file as a **startup instruction set** for AI coding agents working in this repository.

Also read:
- `AGENTS.md` for detailed project rules and Akka coding constraints
- `skills/README.md` for local task routing, especially requirements-first decomposition through `akka-solution-decomposition`
- `docs/agent-coverage-matrix.md` when the task touches agent coverage, gaps, or local example selection

Treat this repository as a **requirements-first, intent-driven Akka pack for AI coding agents**.

Default repository story:
1. read a high-level input such as a PDR, requirements doc, user story, process description, API sketch, or UI brief
2. decompose it into the right Akka architecture
3. route to the focused skills needed for each part of the solution
4. generate code and tests component by component

Component-family skills are important, but they are **downstream implementation assets**, not the only front door.

---

## 1. Mission

This repository exists to provide **Akka SDK resources optimized for AI coding agents working from high-level intent**.

Primary objective:
- help a future agent start from requirements or specification input
- derive the minimal correct Akka component set
- load only the focused skills needed for the current task
- generate correct Akka SDK code and tests with **high accuracy, low ambiguity, and low token cost**

This is the most important distinction in the project:
- **`akka-context/` contains external Akka SDK documentation optimized for human consumption**
- **this repository exists to transform that knowledge into a decomposition-first implementation system for AI coding agents**

Therefore:
- external docs are important for Akka semantics, API correctness, and feature coverage
- local skills and local examples are the preferred source for **agent-oriented implementation patterns**
- component-specific skills should usually be reached **after** intent analysis and solution decomposition

If there is a choice between:
- a human-friendly explanation style, and
- an agent-friendly structure,

prefer the **agent-friendly structure**.

---

## 2. Core operating principle

Decompose before implementation. Optimize for agents, not humans.

That means favor:
- requirements-first routing
- small focused files
- stable naming
- explicit patterns
- predictable package structure
- composable skills
- examples that isolate one topic or one closely related topic pair
- tests that double as pattern references
- routing guidance that tells an agent exactly what to read next

Avoid:
- jumping straight into a component family before the architecture is clear
- large narrative tutorials
- broad mixed-concern examples
- clever abstractions that hide the pattern
- docs that are pleasant for humans to browse but expensive for agents to load
- examples that require reading many unrelated files before they become useful

---

## 3. How to use `akka-context/`

Treat `akka-context/`, especially `akka-context/sdk/`, as the **semantic source of truth for the Akka SDK**.

Use it for:
- component semantics
- API confirmation
- feature behavior
- edge-case rules
- official terminology
- gaps in local repository coverage

But do **not** copy its style blindly.

Important distinction:
- the docs in `akka-context/sdk/` are written for humans
- many Java examples there are also written in a human-oriented style
- this repository should convert those concepts into **AI-oriented patterns** that are easier to reuse mechanically

So the correct workflow is:
1. read `akka-context/sdk/...` when you need official semantics or feature details
2. extract the essential Akka rules
3. implement or document them here in a more agent-optimized form
4. prefer fine-grained local skills and examples over long human-style walkthroughs

In short:
- **`akka-context/` explains Akka**
- **this repository packages Akka knowledge for agent use**

---

## 4. Repository objective hierarchy

When creating or revising repository content, optimize in this order:

1. **agent usefulness**
   - does this help a future coding agent choose the right pattern quickly?
2. **token efficiency**
   - can the future agent succeed after reading only a few focused files?
3. **correct Akka semantics**
   - is the guidance consistent with `akka-context/sdk/` and current Akka SDK behavior?
4. **consistency with repository conventions**
   - does it match local naming, granularity, and package layout?
5. **human readability**
   - important, but secondary to the above

---

## 5. Current scope vs target scope

### Current scope

At the moment, the repository has the deepest local coverage for:
- Event Sourced Entities
- Key Value Entities

It also now has focused local skill and example coverage for:
- agents
  - single-purpose agents with explicit prompts and one public command handler
  - structured responses with `responseConformsTo(...)` and fallback handling
  - function tools via `@FunctionTool` and external tool classes
  - bounded session memory and shared workflow-backed session ids
  - streaming token responses exposed through HTTP endpoints
  - workflow-supervised multi-agent orchestration
  - custom guardrail implementations and config-driven runtime enforcement
  - evaluator agents implementing `EvaluationResult`
  - deterministic testing with `TestModelProvider`
- workflows
  - straight-through orchestration
  - compensating flows with retry-safe downstream command ids
  - workflow notification streams and SSE endpoint exposure
  - pause/resume approval flows
  - workflow integration testing via `componentClient.forWorkflow(...)`
  - workflow-backed consumers and views
- HTTP endpoints
  - component-calling endpoints
  - request-context endpoints
  - static content endpoints
  - co-hosted web UI pages with minimal TypeScript-authored browser logic
  - low-level request/response endpoints
  - endpoint-to-HTTP-service delegation via `HttpClientProvider`
  - SSE endpoints and reconnect testing
  - browser-facing SSE pages backed by Akka streams
  - view-backed SSE endpoints
  - WebSocket endpoints
  - browser-facing WebSocket pages backed by packaged assets
  - JWT-protected endpoints and bearer-token test patterns
  - internal-only ACL endpoints and method-level overrides
  - endpoint integration testing
- gRPC endpoints
  - unary protobuf endpoints
  - component-calling endpoints
  - request-context and ACL-aware endpoints
  - JWT-protected endpoints and bearer-token test patterns
  - server-streaming endpoints
  - gRPC integration testing
  - protobuf evolution and common-type examples
- MCP endpoints
  - component-calling tools that adapt Akka state into LLM-friendly JSON
  - packaged static resources and dynamic URI-template resources
  - prompt templates with explicit parameter descriptions
  - request-context and JWT-aware MCP endpoints
  - direct method testing for MCP tools, resources, and prompts
- timed actions
  - timer registration and deletion patterns
  - timer-safe target command handling for obsolete executions
  - stateless timed action components that call entities or workflows through `ComponentClient`
  - self-rescheduling timed actions via `timers()` inside the handler
  - workflow-triggered timeout timers that feed timeout commands back into paused workflows
  - unit testing with `TimedActionTestkit`
  - integration testing of timer-triggered expiry flows
- consumers
  - event sourced entity consumers
  - key value entity consumers
  - workflow consumers
  - topic-ingesting consumers
  - producing consumers for broker topics and service streams
  - service-to-service subscriber consumer patterns
  - dedicated service-to-service consumer reference docs
  - dedicated service-to-service view reference docs
  - consumer integration testing
- notifications
- TTL
- replication
- unit testing
- integration testing
- view component patterns
  - service-to-service subscriber view docs
  - focused local service-stream view skill routing
  - event sourced views
  - key value views
  - workflow views
  - topic-backed views
  - streaming view queries
  - delete handlers and snapshots

Do not mistake current depth for final scope.

### Target scope

This repository is intended to become a broader Akka SDK reference set for coding agents.

Expected Akka component coverage includes:
- Agents
- Event Sourced Entities
- Key Value Entities
- Views
- Workflows
- Consumers
- Timed Actions
- HTTP Endpoints
- gRPC Endpoints
- MCP Endpoints
- service setup / bootstrap patterns

Expected cross-cutting coverage includes topics such as:
- security
- observability
- local testing
- deployment
- configuration
- environment-specific setup
- documentation/snippet design for agent use

Therefore:
- do not assume every task is entity-centric
- do not force-fit non-entity tasks into current entity skill families
- if local coverage is missing, create new focused skills/examples instead of stretching unrelated ones

---

## 6. How to interpret `skills`

`skills/` is the repository's **source-of-truth skill library** and agent routing layer.

Interpret it this way:
- in this repository, `skills/` is the authored source path
- in installed target projects, these files are copied into `.agents/skills/`
- do not treat `skills/` as Pi-specific runtime state for this repository

A skill should help answer:
- what kind of task is this?
- which files should I read first?
- which pattern applies?
- what companion skills should I load next?

Design expectations for skills:
- one broad entry skill per area
- one requirements-first decomposition skill for solution architecture selection
- smaller companion skills for narrow topics
- explicit "when to use" guidance
- exact file references where possible
- minimal overlap
- low reading cost

When adding a new topic area, prefer:
- one top-level skill
- several narrow companion skills
- one small canonical example set in `src`
- matching tests where practical

---

## 7. How to interpret `src`

`src` is the executable example layer.

Current default structure:
- `src/main/java/com/example/domain`
- `src/main/java/com/example/application`
- `src/main/java/com/example/api`
- `src/test/java/com/example/...`

Interpretation:
- `domain` = pure domain modeling and decision logic
- `application` = Akka components and component-facing orchestration
- `api` = edge-facing API code
- `test` = verification plus reusable execution patterns for future agents

Tests are part of the repository's agent reference set.
They should demonstrate:
- success behavior
- validation behavior
- no-op or idempotent behavior
- integration behavior
- intended calling patterns

---

## 8. Current repository status

Current footprint during this review:
- `88` skill directories under `skills`
- `122` Java source files under `src/main/java`
- `89` test files under `src/test/java`

Current strongest local example areas:
- focused agent component patterns
  - single-purpose prompt-driven agents
  - structured responses and fallback mapping
  - external and agent-local function tools
  - bounded session memory
  - streaming agent replies through HTTP endpoints
  - prompt-template-backed agents, prompt history views, and prompt management endpoints
  - workflow-supervised multi-agent coordination, including dynamic planning and endpoint exposure
  - session-memory consumers, views, compaction flows, topic audit events, and alert SSE endpoints
  - evaluator agents and guardrail examples
  - deterministic testing with `TestModelProvider`
- stateful entity comparison patterns
- endpoint-to-entity flows
- focused HTTP endpoint component patterns
  - request mapping and validation
  - request-context access
  - request headers and JWT claims
  - packaged static content and OpenAPI publication
  - co-hosted web UI pages with packaged HTML/CSS/JS assets
  - TypeScript-authored browser code served as packaged JavaScript assets
  - low-level request/response handling
  - endpoint-to-HTTP-service delegation
  - SSE streaming and reconnect tests
  - browser-facing SSE pages wired to Akka stream routes
  - view-backed SSE streaming
  - WebSocket routes and tests
  - browser-facing WebSocket pages wired to packaged UI routes
  - internal-only ACL examples
  - endpoint integration testing
- focused gRPC endpoint component patterns
  - protobuf-first contract design
  - unary request/reply mapping
  - service-only ACLs and request-context access
  - JWT claim validation, including regex-based claim patterns, and request-context claim access
  - server-streaming responses
  - gRPC integration testing with generated clients
  - schema-evolution and common protobuf type examples
- focused MCP endpoint component patterns
  - component-backed tools returning compact JSON summaries for LLMs
  - packaged markdown resources and dynamic structured resources
  - prompt templates with explicit parameter descriptions and model-friendly structure
  - class-level JWT and request-context-aware MCP endpoints
  - direct method tests with stubbed `McpRequestContext`
- workflow component patterns
  - straight-through step orchestration
  - compensation with explicit result types and idempotent downstream commands
  - workflow notification streams exposed through HTTP SSE
  - pause/resume approval flows
  - workflow integration testing through `componentClient.forWorkflow(...)`
- timed action and timer patterns
  - endpoint-driven timer registration and deletion
  - workflow-driven timeout registration from workflow command handlers
  - timed action components that normalize obsolete executions to `done()`
  - self-rescheduling timers driven from `timers()` inside a timed action
  - timer-safe entity and workflow command design for at-least-once execution
  - unit testing with `TimedActionTestkit`
  - end-to-end expiry verification with `Awaitility`
- focused consumer component patterns
  - event sourced consumers that trigger downstream entities
  - key value consumers driven by latest-state updates and delete handlers
  - topic consumers that route CloudEvent messages into entities
  - consumers that publish to broker topics with `ce-subject` metadata
  - service-stream producer consumers with explicit public contracts and ACLs
  - service-to-service subscriber consumer snippets using `@Consume.FromServiceStream`
  - workflow consumers that publish completion notifications
  - consumer integration tests using mocked incoming and outgoing eventing hooks
- TTL patterns
- replication patterns
- entity testing patterns
- view source/query/testing patterns
  - event sourced views
  - key value views
  - workflow views
  - topic-backed views
  - non-updating and live-updating stream queries
  - delete handlers and snapshot handlers

These are important current references, but they are only the first part of the long-term repository shape.

---

## 9. Session-start behavior for agents

At the start of a new session:

1. Read `AGENTS.md`.
2. Read this file.
3. Read `skills/README.md`.
4. If the task is agent-related, also read `docs/agent-coverage-matrix.md`.
5. Classify the task before choosing files:
   - requirements decomposition / architecture selection
   - component implementation
   - testing
   - docs/snippet generation
   - cross-cutting topic
   - repo structure / skill design
6. If the task starts from high-level requirements, a prompt, or a specification file and the component set is not yet known, start with `akka-solution-decomposition`.
7. If the task seems stateful but the broader component set is still unknown, still start with `akka-solution-decomposition`.
8. Use `akka-entity-type-selection` only when the task is already narrowed to a stateful core and the remaining open question is Event Sourced Entity vs Key Value Entity.
9. Otherwise, check whether a focused local skill already exists.
10. Read the smallest relevant local skill set first.
11. Use `akka-context/sdk/...` when you need official semantics, API confirmation, or a feature not yet well represented locally.
12. Prefer local agent-optimized patterns when generating code or new repository guidance.

---

## 10. Rules for creating new repository content

When adding new skills, docs, or examples:

### Required qualities
- focused
- reusable
- low-ambiguity
- low-token-cost
- easy to route to by file name alone
- easy for a future agent to extend safely

### Preferred file shape
- one topic per file when possible
- one closely related topic pair only when that comparison is the point
- no unnecessary narrative padding
- exact references instead of broad prose where possible

### Preferred example shape
- isolate one pattern
- keep domain logic separate from Akka effect logic
- make the pattern easy to copy into new tasks
- add tests that highlight intended behavior

### Preferred doc shape
- system-prompt-like or routing-oriented when appropriate
- answer "when should I use this?"
- answer "what should I read next?"
- answer "what is the canonical example?"
- avoid long tutorial structure unless the repo specifically needs a teaching asset

---

## 11. What to do when local coverage is missing

If a task belongs to an Akka area or cross-cutting topic that is not yet represented well in this repository:
- consult `akka-context/sdk/...` for official semantics
- do not force the task into an existing ESE/KVE structure unless it truly belongs there
- create or propose a new focused skill family
- create the smallest useful local example set
- preserve current repository conventions: fine-grained, explicit, agent-optimized

The repository should expand by adding many focused modules, not by making existing modules broad and noisy.

---

## 12. Quick decision rules

When unsure, choose the option that:
- reduces the amount of reading required for the next agent
- keeps topics separated
- increases routing clarity
- preserves Akka correctness
- mirrors existing repository conventions
- translates human-oriented Akka documentation into agent-oriented implementation guidance

---

## 13. Bottom line

The most important objective of this project is:

> turn Akka SDK knowledge into skills, examples, and guidance that are optimized for AI coding agents rather than for human readers

Use `akka-context/` for official Akka knowledge.
Use this repository to reshape that knowledge into a form that lets future coding agents work faster, safer, and with fewer tokens.