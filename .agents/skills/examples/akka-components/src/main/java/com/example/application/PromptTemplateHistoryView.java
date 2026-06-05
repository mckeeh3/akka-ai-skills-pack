package com.example.application;

import akka.javasdk.agent.PromptTemplate;
import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.Consume;
import akka.javasdk.annotations.Query;
import akka.javasdk.view.TableUpdater;
import akka.javasdk.view.View;
import java.util.List;

/** View example that tracks current prompt template values together with simple change history. */
@Component(id = "prompt-template-history")
public class PromptTemplateHistoryView extends View {

  public record FindByDeleted(boolean deleted) {}

  public record PromptTemplateHistoryRow(
      String templateId, String currentPrompt, int updateCount, boolean deleted) {}

  public record PromptTemplateHistoryRows(List<PromptTemplateHistoryRow> items) {}

  @Consume.FromEventSourcedEntity(PromptTemplate.class)
  public static class PromptTemplateHistoryUpdater extends TableUpdater<PromptTemplateHistoryRow> {

    public Effect<PromptTemplateHistoryRow> onEvent(PromptTemplate.Event event) {
      var templateId = updateContext().eventSubject().orElse("");
      var current = rowState();

      return switch (event) {
        case PromptTemplate.Event.Updated updated -> {
          var count = current == null ? 1 : current.updateCount() + 1;
          yield effects()
              .updateRow(new PromptTemplateHistoryRow(templateId, updated.prompt(), count, false));
        }
        case PromptTemplate.Event.Deleted ignored -> {
          var count = current == null ? 0 : current.updateCount();
          var prompt = current == null ? "" : current.currentPrompt();
          yield effects().updateRow(new PromptTemplateHistoryRow(templateId, prompt, count, true));
        }
      };
    }

  }

  @Query(
      """
      SELECT * AS items
      FROM prompt_template_history
      WHERE deleted = :deleted
      ORDER BY templateId
      """)
  public QueryEffect<PromptTemplateHistoryRows> getByDeleted(FindByDeleted request) {
    return queryResult();
  }
}
