package com.example.api;

import akka.http.javadsl.model.HttpResponse;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.client.ComponentClient;
import akka.javasdk.http.HttpResponses;
import com.example.application.PromptTemplateHistoryView;
import java.util.List;

/** HTTP endpoint example for querying prompt-template history rows through a view. */
@HttpEndpoint("/agent-prompts/history")
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.ALL))
public class PromptTemplateHistoryEndpoint {

  public record PromptTemplateHistoryItem(
      String templateId, String currentPrompt, int updateCount, boolean deleted) {}

  public record PromptTemplateHistoryResponse(List<PromptTemplateHistoryItem> items) {}

  private final ComponentClient componentClient;

  public PromptTemplateHistoryEndpoint(ComponentClient componentClient) {
    this.componentClient = componentClient;
  }

  @Get("/{deleted}")
  public HttpResponse getByDeleted(boolean deleted) {
    var result =
        componentClient
            .forView()
            .method(PromptTemplateHistoryView::getByDeleted)
            .invoke(new PromptTemplateHistoryView.FindByDeleted(deleted));

    var items =
        result.items().stream()
            .map(
                row ->
                    new PromptTemplateHistoryItem(
                        row.templateId(), row.currentPrompt(), row.updateCount(), row.deleted()))
            .toList();

    return HttpResponses.ok(new PromptTemplateHistoryResponse(items));
  }
}
