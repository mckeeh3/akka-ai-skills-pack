package com.example.application;

import akka.javasdk.agent.task.Task;
import akka.javasdk.annotations.Description;

/** Task definition and typed result for the Autonomous Agent handoff triage example. */
public final class HandoffTriageTasks {

  private HandoffTriageTasks() {}

  public static final Task<SupportResolution> RESOLVE =
      Task.name("ResolveSupportRequest")
          .description("Resolve a support request after triage and optional specialist handoff")
          .resultConformsTo(SupportResolution.class);

  public record SupportResolution(
      @Description("Specialist category that resolved the request") String category,
      @Description("Resolution summary safe to show to the caller") String resolution,
      @Description("Whether the request was resolved") boolean resolved) {}
}
