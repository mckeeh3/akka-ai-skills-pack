package com.example.api;

import akka.http.javadsl.model.HttpResponse;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.annotations.http.Post;
import akka.javasdk.http.HttpResponses;
import java.util.List;

/** Browser-facing JSON API for the lightweight frontend reference app. */
@HttpEndpoint("/api/frontend-reference")
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.ALL))
public class FrontendReferenceApiEndpoint {

  public record RequestRow(String id, String title, String requester, String status, int amount) {}

  public record DashboardResponse(
      List<RequestRow> requests,
      List<String> allowedStatuses,
      String streamPath,
      String submitPath) {}

  public record SubmitRequest(String title, String requester, int amount) {}

  public record SubmitResponse(RequestRow request, String message) {}

  @Get("/dashboard")
  public DashboardResponse dashboard() {
    return new DashboardResponse(
        List.of(
            new RequestRow("REQ-1001", "Team laptop refresh", "Ada", "Pending", 4200),
            new RequestRow("REQ-1002", "Conference travel", "Grace", "Approved", 1800),
            new RequestRow("REQ-1003", "Security training", "Linus", "Needs changes", 600)),
        List.of("Pending", "Approved", "Needs changes"),
        "/counter-stream/current",
        "/api/frontend-reference/requests");
  }

  @Post("/requests")
  public HttpResponse submit(SubmitRequest request) {
    if (request == null) {
      return HttpResponses.badRequest("request body is required");
    }
    if (request.title() == null || request.title().isBlank()) {
      return HttpResponses.badRequest("title is required");
    }
    if (request.requester() == null || request.requester().isBlank()) {
      return HttpResponses.badRequest("requester is required");
    }
    if (request.amount() <= 0) {
      return HttpResponses.badRequest("amount must be greater than zero");
    }

    return HttpResponses.created(
        new SubmitResponse(
            new RequestRow("REQ-DRAFT", request.title().trim(), request.requester().trim(), "Pending", request.amount()),
            "Request accepted by the browser-facing API"));
  }
}
