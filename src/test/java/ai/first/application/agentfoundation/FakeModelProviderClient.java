package ai.first.application.agentfoundation;

/** Clearly named unit-test fake. Do not wire this as the normal runtime model provider. */
final class FakeModelProviderClient implements ModelProviderClient {
  private final ModelProviderResponse response;
  ModelProviderRequest lastRequest;

  FakeModelProviderClient(String markdown) {
    this.response = new ModelProviderResponse(markdown, "test-fake-provider", "test-fake-model", "fake-response-id", "stop", "unit-test fake response");
  }

  @Override
  public ModelProviderResponse invoke(ModelProviderRequest request) {
    lastRequest = request;
    return response;
  }
}
