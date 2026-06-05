# Skills Pack Tech Stack

Applications maintained with this skills pack target a full-stack architecture with **Akka on the backend** and **React/Vite/TypeScript on the frontend**. The stack is designed for AI agents that need to turn high-level product intent into durable application descriptions, implementation plans, and maintained implementation across backend, frontend, tests, and operational concerns.

At the backend core is the **Akka SDK**, using Akka components as peer building blocks rather than treating the app as a single layered CRUD service. The supported backend shape includes:

- **Event Sourced Entities** for command-driven domain models where auditability, event history, temporal reasoning, replay, and behavior over time matter.
- **Key Value Entities** for simpler stateful records where the current state is the primary concern.
- **Views** for CQRS-style read models, projections, query APIs, dashboards, filtered lists, and UI-facing state.
- **Workflows** for long-running business processes, orchestration, compensation, pause/resume flows, approvals, retries, and multi-step automation.
- **Consumers** for reacting to entity, workflow, service stream, or topic events.
- **Timed Actions and timers** for scheduled work, reminders, expirations, retry loops, and deadline-driven processes.
- **HTTP endpoints** for browser and API clients, including JSON APIs, streaming, SSE, WebSocket, and React/Vite/TypeScript web UI delivery.
- **gRPC endpoints** where typed service contracts or internal service-to-service APIs are needed.
- **MCP endpoints** where the application exposes tools, resources, or prompts to AI clients.

The stack assumes an **event-driven and CQRS-oriented design**. Commands mutate state through entities and workflows. Events and state changes feed views, consumers, notifications, audit trails, analytics, and UI read models. Harness skills should therefore design applications around explicit write paths, read paths, asynchronous reactions, and process orchestration instead of defaulting to generic CRUD.

AI agents are a first-class backend capability. The stack supports Akka agents that can:

- encapsulate single-purpose LLM responsibilities,
- use structured responses,
- call local tools and external tool classes,
- use Akka components as tools,
- call remote MCP tools,
- participate in workflow orchestration,
- use session memory and bounded memory reads,
- stream responses to clients,
- process multimodal input,
- apply guardrails,
- support evaluator/LLM-as-judge patterns,
- expose and manage runtime prompt state,
- integrate with deterministic tests through model providers.

Agentic applications may combine deterministic Akka components with LLM-powered reasoning. A typical design may use entities for durable business state, workflows for orchestration, views for queryable state, consumers for event reactions, and agents for planning, classification, summarization, recommendations, document analysis, tool use, or human-assist automation.

The frontend stack is **React + Vite + TypeScript**, integrated as part of the same full-stack application model. The skills support frontend design and implementation for:

- browser UI projects,
- typed API clients,
- forms and validation,
- state rendering,
- real-time UI updates through SSE/WebSocket/streaming endpoints,
- accessibility and responsive design,
- UX flows derived from the same app description used for backend generation,
- Akka-hosted frontend build-output delivery where appropriate.

For harness agents creating higher-level app-design skills, this means the target stack should be described as a **single full-stack system**, not as disconnected backend and frontend tasks. Product capabilities should be decomposed into:

1. domain state and commands,
2. events and asynchronous reactions,
3. CQRS read models and views,
4. workflows and long-running processes,
5. AI-agent responsibilities and tool boundaries,
6. HTTP/gRPC/MCP/API surfaces,
7. React/Vite/TypeScript agent workstream shell, structured surfaces, and client contracts,
8. tests and acceptance criteria across backend, frontend, and agent behavior.

In short: this skills pack supports designing, extending, and realizing **modern full-stack Akka applications**: event-driven, CQRS-friendly, workflow-capable, AI-agent-native, and integrated with TypeScript React frontends.
