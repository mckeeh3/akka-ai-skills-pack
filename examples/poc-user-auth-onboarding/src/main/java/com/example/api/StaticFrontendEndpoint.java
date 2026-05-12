package com.example.api;

import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.http.AbstractHttpEndpoint;
import akka.javasdk.http.HttpResponses;


@Acl(allow = @Acl.Matcher(principal = Acl.Principal.INTERNET))
@HttpEndpoint
public class StaticFrontendEndpoint extends AbstractHttpEndpoint {

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
