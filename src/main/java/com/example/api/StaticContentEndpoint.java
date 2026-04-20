package com.example.api;

import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.http.HttpResponses;

/**
 * Focused HTTP endpoint example for serving packaged static resources.
 */
@HttpEndpoint("/static-content")
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.ALL))
public class StaticContentEndpoint {

  @Get("/")
  public HttpResponse index() {
    return HttpResponses.staticResource("http-endpoint/index.html");
  }

  @Get("/app.css")
  public HttpResponse appCss() {
    return HttpResponses.staticResource("http-endpoint/app.css");
  }

  @Get("/bundle/**")
  public HttpResponse bundle(HttpRequest request) {
    return HttpResponses.staticResource(request, "/static-content/bundle/");
  }

  @Get("/openapi.yaml")
  public HttpResponse openApiSpecification() {
    return HttpResponses.staticResource("http-endpoint/openapi.yaml");
  }
}
