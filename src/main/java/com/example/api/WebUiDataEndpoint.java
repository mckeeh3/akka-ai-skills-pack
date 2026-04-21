package com.example.api;

import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import java.util.List;

/** HTTP endpoint example exposing browser-facing JSON for the packaged web UI. */
@HttpEndpoint("/api/web-ui")
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.ALL))
public class WebUiDataEndpoint {

  public record WebUiSummaryResponse(
      String title, String message, List<String> capabilities, String apiPath) {}

  @Get("/summary")
  public WebUiSummaryResponse summary() {
    return new WebUiSummaryResponse(
        "Co-hosted Akka web UI",
        "This page is packaged with the service and loads JSON through a dedicated /api route.",
        List.of(
            "Packaged HTML, CSS, and JavaScript served by Akka",
            "Framework-free browser code authored in TypeScript",
            "Explicit separation between /ui and /api route families"),
        "/api/web-ui/summary");
  }
}
