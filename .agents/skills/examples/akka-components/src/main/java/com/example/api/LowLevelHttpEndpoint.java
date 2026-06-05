package com.example.api;

import akka.http.javadsl.model.ContentType;
import akka.http.javadsl.model.ContentTypes;
import akka.http.javadsl.model.HttpEntity;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.MediaTypes;
import akka.http.javadsl.model.StatusCodes;
import akka.javasdk.JsonSupport;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.annotations.http.Post;
import akka.javasdk.http.HttpResponses;

/**
 * Focused HTTP endpoint example for lower-level request and response handling.
 */
@HttpEndpoint("/low-level")
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.ALL))
public class LowLevelHttpEndpoint {

  private static final ContentType IMAGE_JPEG = ContentTypes.create(MediaTypes.IMAGE_JPEG);

  public record HelloResponse(String greeting) {}

  public record UploadSummaryResponse(String name, int bytes, String contentType) {}

  @Get("/hello/{name}/{age}")
  public HttpResponse hello(String name, int age) {
    if (age > 130) {
      return HttpResponse.create()
          .withStatus(StatusCodes.BAD_REQUEST)
          .withEntity("It is unlikely that you are " + age + " years old");
    }

    var jsonBytes = JsonSupport.encodeToAkkaByteString(new HelloResponse("Hello " + name + "!"));
    return HttpResponse.create().withEntity(ContentTypes.APPLICATION_JSON, jsonBytes);
  }

  @Post("/images/{name}")
  public HttpResponse uploadImage(String name, HttpEntity.Strict strictRequestBody) {
    if (!strictRequestBody.getContentType().equals(IMAGE_JPEG)) {
      return HttpResponses.badRequest("This service only accepts " + IMAGE_JPEG);
    }

    return HttpResponses.ok(
        new UploadSummaryResponse(
            name,
            strictRequestBody.getData().size(),
            strictRequestBody.getContentType().toString()));
  }
}
