package com.example.api;

import akka.http.javadsl.model.HttpResponse;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.annotations.http.Patch;
import akka.javasdk.annotations.http.Post;
import akka.javasdk.http.AbstractHttpEndpoint;
import akka.javasdk.http.HttpResponses;
import java.util.Locale;

/**
 * Focused HTTP endpoint example for request mapping and request-context access.
 *
 * <p>This endpoint is intentionally independent of other Akka components so it can be used as a
 * small reference for:
 *
 * <ul>
 *   <li>path parameters</li>
 *   <li>request body mapping</li>
 *   <li>query parameter access through {@link #requestContext()}</li>
 *   <li>explicit HTTP validation responses</li>
 * </ul>
 */
@HttpEndpoint("/greetings")
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.ALL))
public class GreetingEndpoint extends AbstractHttpEndpoint {

  public record ComposeGreetingRequest(String name) {}

  public record GreetingResponse(String message, String language, boolean shouted) {}

  @Get("/hello/{name}")
  public HttpResponse hello(String name) {
    if (name == null || name.isBlank()) {
      return HttpResponses.badRequest("name must not be blank");
    }

    var language = requestContext().queryParams().getString("language").orElse("en");
    var shouted = requestContext().queryParams().getString("style").orElse("").equals("shout");

    return HttpResponses.ok(new GreetingResponse(formatGreeting(name, language, shouted), language, shouted));
  }

  @Post("/compose/{language}")
  public HttpResponse compose(String language, ComposeGreetingRequest request) {
    if (request == null || request.name() == null || request.name().isBlank()) {
      return HttpResponses.badRequest("name must not be blank");
    }
    if (language == null || language.isBlank()) {
      return HttpResponses.badRequest("language must not be blank");
    }

    return HttpResponses.ok(new GreetingResponse(formatGreeting(request.name(), language, false), language, false));
  }

  @Patch("/hello/{name}/shout")
  public HttpResponse shout(String name) {
    if (name == null || name.isBlank()) {
      return HttpResponses.badRequest("name must not be blank");
    }

    return HttpResponses.ok(new GreetingResponse(formatGreeting(name, "en", true), "en", true));
  }

  private static String formatGreeting(String name, String language, boolean shouted) {
    var greeting =
        switch (language) {
          case "sv" -> "Hej " + name + "!";
          case "es" -> "Hola " + name + "!";
          default -> "Hello " + name + "!";
        };

    return shouted ? greeting.toUpperCase(Locale.ROOT) : greeting;
  }
}
