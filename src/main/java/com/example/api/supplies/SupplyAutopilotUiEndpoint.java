package com.example.api.supplies;

import akka.http.javadsl.model.HttpResponse;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.http.HttpResponses;

/** Serves the supplies autopilot command-center and decision-card reference UI. */
@HttpEndpoint
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.ALL))
public class SupplyAutopilotUiEndpoint {

  @Get("/ui/supplies")
  public HttpResponse index() {
    return HttpResponses.staticResource("supplies/index.html");
  }

  @Get("/ui/supplies/app.css")
  public HttpResponse appCss() {
    return HttpResponses.staticResource("supplies/app.css");
  }

  @Get("/ui/supplies/app.js")
  public HttpResponse appJs() {
    return HttpResponses.staticResource("supplies/app.js");
  }
}
