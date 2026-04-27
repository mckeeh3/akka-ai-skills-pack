package com.example.api;

import akka.http.javadsl.model.HttpResponse;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.http.HttpResponses;

/** Serves a feature-complete lightweight TypeScript frontend reference app. */
@HttpEndpoint
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.ALL))
public class FrontendReferenceUiEndpoint {

  @Get("/ui/frontend-reference")
  public HttpResponse index() {
    return HttpResponses.staticResource("frontend-reference/index.html");
  }

  @Get("/ui/frontend-reference/app.css")
  public HttpResponse appCss() {
    return HttpResponses.staticResource("frontend-reference/app.css");
  }

  @Get("/ui/frontend-reference/app.js")
  public HttpResponse appJs() {
    return HttpResponses.staticResource("frontend-reference/app.js");
  }

  @Get("/ui/frontend-reference/api.js")
  public HttpResponse apiJs() {
    return HttpResponses.staticResource("frontend-reference/api.js");
  }

  @Get("/ui/frontend-reference/dom.js")
  public HttpResponse domJs() {
    return HttpResponses.staticResource("frontend-reference/dom.js");
  }

  @Get("/ui/frontend-reference/forms.js")
  public HttpResponse formsJs() {
    return HttpResponses.staticResource("frontend-reference/forms.js");
  }

  @Get("/ui/frontend-reference/render.js")
  public HttpResponse renderJs() {
    return HttpResponses.staticResource("frontend-reference/render.js");
  }

  @Get("/ui/frontend-reference/state.js")
  public HttpResponse stateJs() {
    return HttpResponses.staticResource("frontend-reference/state.js");
  }
}
