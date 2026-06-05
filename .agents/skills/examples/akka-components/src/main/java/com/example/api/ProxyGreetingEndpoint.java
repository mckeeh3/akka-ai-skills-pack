package com.example.api;

import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.HttpHeader;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.http.AbstractHttpEndpoint;
import akka.javasdk.http.HttpClientProvider;
import akka.javasdk.http.HttpResponses;

/**
 * Focused HTTP endpoint example for delegating to another HTTP service through HttpClientProvider.
 */
@HttpEndpoint("/proxy-greetings")
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.ALL))
public class ProxyGreetingEndpoint extends AbstractHttpEndpoint {

  public record GreetingResponse(String message, String language, boolean shouted) {}

  public record DelegatedGreetingResponse(String message, String delegatedTo) {}

  private final HttpClientProvider httpClientProvider;

  public ProxyGreetingEndpoint(HttpClientProvider httpClientProvider) {
    this.httpClientProvider = httpClientProvider;
  }

  @Get("/hello/{name}")
  public HttpResponse hello(String name) {
    var baseUrl =
        requestContext().requestHeader("X-Base-Url").map(HttpHeader::value).orElse("");
    if (baseUrl.isBlank()) {
      return HttpResponses.badRequest("X-Base-Url header is required");
    }

    var language = requestContext().queryParams().getString("language").orElse("en");

    var upstream =
        httpClientProvider
            .httpClientFor(baseUrl)
            .GET("/greetings/hello/" + name)
            .addQueryParameter("language", language)
            .responseBodyAs(GreetingResponse.class)
            .invoke();

    return HttpResponses.ok(new DelegatedGreetingResponse(upstream.body().message(), baseUrl));
  }
}
