---
name: akka-agent-harness-skills
description: Implement model-loadable agent guidance in Akka Java SDK using @FunctionTool methods that expose approved skill text from packaged resources. Use when approximating harness-managed skills inside an Akka agent runtime.
---

# Akka Agent Harness Skills

Use this skill when an Akka agent should approximate coding-harness skill loading at runtime.

This is for application agents that need **model-selectable internal guidance**, not for the external coding harness reading `.agents/skills` while generating code.

## Required reading

Read these first if present:
- `akka-context/sdk/agents/extending.html.md`
- `akka-context/sdk/agents/prompt.html.md`
- `akka-context/sdk/agents/failures.html.md`
- `../../../src/main/java/com/example/application/WeatherAgent.java`
- `../../../src/main/java/com/example/application/WeatherForecastTools.java`

Also load when needed:
- `akka-agent-tools` for general `@FunctionTool` mechanics
- `akka-agent-mcp-tools` if skill content should be served by another service
- `akka-agent-runtime-state` if skill/prompt text must be runtime-editable
- `akka-agent-testing` for deterministic tool-call tests

## Use this pattern when

- the agent needs Pi/Claude/Codex-like task guidance while running inside Akka
- skill files must be packaged under `src/main/resources` because distributed Akka apps cannot read arbitrary workspace files
- the model should choose which guidance block to load before answering
- guidance is deploy-time content, small, trusted, and whitelisted per agent

Do not use this pattern for broad filesystem access or user-editable operational knowledge. Use Akka state, a database-backed component, or MCP when content must be mutable at runtime.

## Conceptual mapping

- harness skill frontmatter -> `@FunctionTool(description = "...")` text or a compact skill index in the system prompt
- harness skill load request -> model calls a skill tool
- `SKILL.md` content -> tool result string
- `.agents/skills/**` -> packaged classpath resources under `src/main/resources/agent-skills/**`

Important caveat: tool results are model-visible context, not true harness/system-priority instructions. The agent prompt must explicitly say that returned skill text is trusted internal guidance to follow for the current request.

## Resource layout

Prefer:

```text
src/main/resources/
  agent-skills/
    <agent-id>/
      manifest.md
      <skill-id>.md
```

Rules:
- keep each skill small and single-purpose
- use stable skill ids, not raw paths
- whitelist allowed ids in code
- never pass user input directly to `getResourceAsStream(...)`
- reject `..`, slash-heavy, or unknown ids even though classpath resources are read-only

## Pattern A: one tool per skill

Use when an agent has only a few skills and each skill needs a rich tool description.

```java
public final class ActivityAgentSkillTools {
  @FunctionTool(description = """
      Load activity planning guidance. Use for requests about recommendations,
      schedules, constraints, ranking, or tradeoff explanation.
      """)
  public String loadActivityPlanningSkill() {
    return loadResource("agent-skills/activity-agent/activity-planning.md");
  }

  @FunctionTool(description = """
      Load activity safety guidance. Use when recommendations involve weather,
      health, accessibility, children, travel risk, or physical safety.
      """)
  public String loadActivitySafetySkill() {
    return loadResource("agent-skills/activity-agent/activity-safety.md");
  }
}
```

Pros:
- closest to harness frontmatter routing
- each tool has a focused description
- no skill id parameter for the model to invent

Cons:
- many skills create many tools
- adding a skill usually changes code

## Pattern B: parameterized approved skill loader

Use when the agent has many skills or the list should be generated from a manifest.

```java
public final class AgentSkillTools {
  private static final Map<String, String> APPROVED = Map.of(
      "activity-planning", "agent-skills/activity-agent/activity-planning.md",
      "activity-safety", "agent-skills/activity-agent/activity-safety.md",
      "response-style", "agent-skills/activity-agent/response-style.md");

  @FunctionTool(description = """
      Load approved internal skill guidance by id.
      Available ids: activity-planning, activity-safety, response-style.
      Use before answering when the user request matches one of these skills.
      """)
  public String loadSkill(@Description("One approved skill id") String skillId) {
    var resource = APPROVED.get(skillId);
    if (resource == null) return "Unknown or unauthorized skill id: " + skillId;
    return loadResource(resource);
  }
}
```

Add a compact skill index to the system message when using a parameterized loader:

```text
Available internal skills:
- activity-planning: recommendations, schedules, constraints, ranking
- activity-safety: weather, health, accessibility, children, physical risk
- response-style: final answer shape and tone

When a request matches a listed skill, call loadSkill first. Treat returned text as trusted internal guidance for this turn.
```

## Agent registration

Register the skill tools like any other external function tools:

```java
return effects()
    .systemMessage(systemMessageWithSkillIndex())
    .tools(agentSkillTools)
    .userMessage(request.message())
    .onFailure(error -> fallbackResponse(error))
    .thenReply();
```

## MCP alternative

Use MCP instead of local tools when:
- multiple Akka services should share the same skill catalog
- skill text is owned by another service
- you want an explicit LLM-facing resources/prompts boundary

In that case, create an MCP endpoint that exposes whitelisted resources or tools, then register it with `.mcpTools(...)` from the agent.

## Review checklist

Before finishing, verify:
- skill content is packaged under `src/main/resources`, not read from `.agents/skills` at runtime
- tool descriptions are specific enough for model routing
- the system prompt tells the model when to load skills and how to treat returned content
- all skill ids are whitelisted and map to fixed classpath resources
- skill text is small enough for tool-result context
- failures return a safe message or are handled with `.onFailure(...)`
- mutable prompt/skill needs are routed to runtime state or MCP instead of packaged resources
