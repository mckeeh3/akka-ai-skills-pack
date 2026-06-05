package com.example.application;

import akka.javasdk.agent.Agent;
import akka.javasdk.agent.ContentLoader;
import akka.javasdk.agent.MessageContent;
import akka.javasdk.agent.UserMessage;
import akka.javasdk.annotations.Component;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Optional;

/** Agent example for multimodal user messages and per-request content loading. */
@Component(
    id = "document-analysis-agent",
    name = "Document Analysis Agent",
    description =
        "Analyzes an image and a PDF together using a multimodal user message and a custom content loader.")
public class DocumentAnalysisAgent extends Agent {

  public record AnalyzeRequest(
      String instruction, String imageUri, String pdfUri, String accessToken) {}

  private static final String SYSTEM_MESSAGE =
      """
      You analyze the provided image and PDF together.
      Summarize the most important visual and document details relevant to the user's instruction.
      Keep the answer concise and practical.
      """
          .stripIndent();

  public Effect<String> analyze(AnalyzeRequest request) {
    var builder = effects().systemMessage(SYSTEM_MESSAGE);
    if (request.accessToken() != null && !request.accessToken().isBlank()) {
      builder = builder.contentLoader(new ExampleContentLoader(request.accessToken()));
    }

    var contents = new ArrayList<MessageContent>();
    contents.add(MessageContent.TextMessageContent.from(request.instruction()));
    if (request.imageUri() != null && !request.imageUri().isBlank()) {
      contents.add(MessageContent.ImageMessageContent.fromUrl(request.imageUri()));
    }
    if (request.pdfUri() != null && !request.pdfUri().isBlank()) {
      contents.add(MessageContent.PdfMessageContent.fromUrl(request.pdfUri()));
    }

    return builder.userMessage(UserMessage.from(contents.toArray(MessageContent[]::new))).thenReply();
  }

  /**
   * Small deterministic content loader used only to demonstrate the API shape for authenticated or
   * custom-backed media.
   */
  public static final class ExampleContentLoader implements ContentLoader {

    private final String accessToken;

    public ExampleContentLoader(String accessToken) {
      this.accessToken = accessToken;
    }

    @Override
    public LoadedContent load(MessageContent.LoadableMessageContent content) {
      return switch (content) {
        case MessageContent.ImageUrlMessageContent image ->
            new LoadedContent(
                ("image-bytes:" + image.url() + ":token=" + accessToken)
                    .getBytes(StandardCharsets.UTF_8),
                Optional.of(image.mimeType().orElse("image/png")));
        case MessageContent.PdfUrlMessageContent pdf ->
            new LoadedContent(
                ("pdf-bytes:" + pdf.url() + ":token=" + accessToken)
                    .getBytes(StandardCharsets.UTF_8),
                Optional.of("application/pdf"));
      };
    }
  }
}
