package com.example.api;

import akka.http.javadsl.model.HttpResponse;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.http.HttpResponses;

/** HTTP endpoint example serving a packaged web UI shell and its static assets. */
@HttpEndpoint
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.ALL))
public class WebUiHomeEndpoint {

  @Get("/ui")
  public HttpResponse index() {
    return HttpResponses.staticResource("web-ui/index.html");
  }

  @Get("/ui/app.css")
  public HttpResponse appCss() {
    return HttpResponses.staticResource("web-ui/app.css");
  }

  @Get("/ui/app.js")
  public HttpResponse appJs() {
    return HttpResponses.staticResource("web-ui/app.js");
  }
}
