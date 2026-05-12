package com.example.api.security;

import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.http.HttpResponses;

/** Serves the authenticated DCA seed React/Vite shell and generated build assets. */
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.INTERNET))
@HttpEndpoint
public class DcaSeedFrontendEndpoint {

  @Get("/")
  public HttpResponse index() {
    return HttpResponses.staticResource("index.html");
  }

  @Get("/favicon.ico")
  public HttpResponse favicon() {
    return HttpResponses.staticResource("favicon.ico");
  }

  @Get("/assets/**")
  public HttpResponse assets(HttpRequest request) {
    return HttpResponses.staticResource(request, "/");
  }
}
