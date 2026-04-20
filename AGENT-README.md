# AGENT-README

Use this file as a **startup instruction set** for AI coding agents working in this repository.

Also read:
- `AGENTS.md` for detailed project rules and Akka coding constraints
- `.pi/skills/README.md` for local task routing

---

## 1. Mission

This repository exists to provide **Akka SDK resources optimized for AI coding agents**.

Primary objective:
- produce skills, code examples, tests, and guidance that help an AI agent generate correct Akka SDK code with **high accuracy, low ambiguity, and low token cost**

This is the most important distinction in the project:
- **`akka-context/` contains external Akka SDK documentation optimized for human consumption**
- **this repository exists to transform that knowledge into resources optimized for AI coding agents**

Therefore:
- external docs are important for Akka semantics, API correctness, and feature coverage
- local skills and local examples are the preferred source for **agent-oriented implementation patterns**

If there is a choice between:
- a human-friendly explanation style, and
- an agent-friendly structure,

prefer the **agent-friendly structure**.

---

## 2. Core operating principle

Optimize for agents, not humans.

That means favor:
- small focused files
- stable naming
- explicit patterns
- predictable package structure
- composable skills
- examples that isolate one topic or one closely related topic pair
- tests that double as pattern references
- routing guidance that tells an agent exactly what to read next

Avoid:
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
- HTTP endpoints
  - component-calling endpoints
  - request-context endpoints
  - static content endpoints
  - low-level request/response endpoints
  - endpoint-to-HTTP-service delegation via `HttpClientProvider`
  - SSE endpoints and reconnect testing
  - view-backed SSE endpoints
  - WebSocket endpoints
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
- consumers
- notifications
- TTL
- replication
- unit testing
- integration testing
- view component patterns
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

## 6. How to interpret `.pi/skills`

`.pi/skills` is the repository's **agent routing layer**.

A skill should help answer:
- what kind of task is this?
- which files should I read first?
- which pattern applies?
- what companion skills should I load next?

Design expectations for skills:
- one broad entry skill per area
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
- `41` skill directories under `.pi/skills`
- `50` Java source files under `src/main/java`
- `37` test files under `src/test/java`

Current strongest local example areas:
- stateful entity comparison patterns
- endpoint-to-entity flows
- focused HTTP endpoint component patterns
  - request mapping and validation
  - request-context access
  - request headers and JWT claims
  - packaged static content and OpenAPI publication
  - low-level request/response handling
  - endpoint-to-HTTP-service delegation
  - SSE streaming and reconnect tests
  - view-backed SSE streaming
  - WebSocket routes and tests
  - internal-only ACL examples
  - endpoint integration testing
- focused gRPC endpoint component patterns
  - protobuf-first contract design
  - unary request/reply mapping
  - service-only ACLs and request-context access
  - JWT claim validation and request-context claim access
  - server-streaming responses
  - gRPC integration testing with generated clients
  - schema-evolution and common protobuf type examples
- downstream consumer flows
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
3. Read `.pi/skills/README.md`.
4. Classify the task before choosing files:
   - component implementation
   - testing
   - docs/snippet generation
   - cross-cutting topic
   - repo structure / skill design
5. Check whether a focused local skill already exists.
6. Read the smallest relevant local skill set first.
7. Use `akka-context/sdk/...` when you need official semantics, API confirmation, or a feature not yet well represented locally.
8. Prefer local agent-optimized patterns when generating code or new repository guidance.

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