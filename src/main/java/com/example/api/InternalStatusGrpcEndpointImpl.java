package com.example.api;

import akka.grpc.GrpcServiceException;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.GrpcEndpoint;
import akka.javasdk.grpc.AbstractGrpcEndpoint;
import com.example.api.grpc.CallerResponse;
import com.example.api.grpc.InternalStatusGrpcEndpoint;
import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import io.grpc.Status;

/** Focused gRPC endpoint example for service-only ACLs and request context access. */
@GrpcEndpoint
@Acl(allow = @Acl.Matcher(service = "*"))
public class InternalStatusGrpcEndpointImpl extends AbstractGrpcEndpoint
    implements InternalStatusGrpcEndpoint {

  @Override
  public CallerResponse whoAmI(Empty in) {
    if (in == null) {
      throw new GrpcServiceException(Status.INVALID_ARGUMENT.augmentDescription("request is required"));
    }

    var principals = requestContext().getPrincipals();
    var builder =
        CallerResponse.newBuilder()
            .setOrigin(origin(principals))
            .setLocalService(principals.getLocalService().orElse(""))
            .setInternet(principals.isInternet())
            .setMetadataEntryCount(requestContext().metadata().asMap().size());

    requestContext().metadata().getText("x-request-id").ifPresent(value -> builder.setRequestId(StringValue.of(value)));

    return builder.build();
  }

  private static String origin(akka.javasdk.Principals principals) {
    if (principals.isSelf()) {
      return "self";
    } else if (principals.isBackoffice()) {
      return "backoffice";
    } else if (principals.isInternet()) {
      return "internet";
    } else {
      return principals.getLocalService().isPresent() ? "service" : "unknown";
    }
  }
}
