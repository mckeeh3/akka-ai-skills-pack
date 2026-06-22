package ai.first.api.coreapp.workstream;

import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.http.AbstractHttpEndpoint;
import akka.javasdk.http.HttpResponses;

/** Serves the packaged React/Vite workstream frontend assets from static resources. */
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.INTERNET))
@HttpEndpoint("/")
public class StarterFrontendEndpoint extends AbstractHttpEndpoint {

  @Get
  public HttpResponse index() {
    return HttpResponses.staticResource("index.html");
  }

  @Get("favicon.ico")
  public HttpResponse favicon() {
    return HttpResponses.staticResource("favicon.ico");
  }

  @Get("assets/**")
  public HttpResponse assets(HttpRequest request) {
    return HttpResponses.staticResource(request, "/");
  }

  @Get("ui")
  public HttpResponse ui() {
    return HttpResponses.staticResource("index.html");
  }

  @Get("workstream")
  public HttpResponse workstream() {
    return HttpResponses.staticResource("index.html");
  }

  @Get("callback")
  public HttpResponse authCallback() {
    return HttpResponses.staticResource("index.html");
  }

  @Get("accept")
  public HttpResponse acceptInvitation() {
    return HttpResponses.staticResource("index.html");
  }

  @Get("invite/accept")
  public HttpResponse inviteAcceptInvitation() {
    return HttpResponses.staticResource("index.html");
  }
}
