package ai.first.application;

import akka.javasdk.agent.task.Task;
import akka.javasdk.annotations.Description;
import java.util.List;

/** Task definitions and typed result records for the Autonomous Agent delegation example. */
public final class ResearchCoordinationTasks {

  private ResearchCoordinationTasks() {}

  public static final Task<ResearchBrief> BRIEF =
      Task.name("ResearchBrief")
          .description("Produce a concise research brief by delegating specialist findings")
          .resultConformsTo(ResearchBrief.class);

  public static final Task<ResearchFindings> FINDINGS =
      Task.name("ResearchFindings")
          .description("Research one focused topic and return factual findings")
          .resultConformsTo(ResearchFindings.class);

  public record ResearchBrief(
      @Description("Brief title") String title,
      @Description("Synthesized answer or recommendation") String summary,
      @Description("Key findings incorporated from delegated worker results") List<String> keyFindings) {}

  public record ResearchFindings(
      @Description("Topic researched by the worker") String topic,
      @Description("Factual findings for the topic") List<String> findings,
      @Description("Source labels or evidence references used by the worker") List<String> sources) {}
}
