package com.example.api;

import akka.http.javadsl.model.HttpResponse;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.http.HttpResponses;

/** HTTP endpoint example serving a packaged page that connects to an SSE route. */
@HttpEndpoint
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.ALL))
public class WebUiSsePageEndpoint {

  @Get("/ui/sse")
  public HttpResponse index() {
    return HttpResponses.staticResource("web-ui-sse/index.html");
  }

  @Get("/ui/sse/app.js")
  public HttpResponse appJs() {
    return HttpResponses.staticResource("web-ui-sse/app.js");
  }
}
