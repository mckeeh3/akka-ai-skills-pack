package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.agent.MessageContent;
import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import akka.javasdk.testkit.TestModelProvider;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class DocumentAnalysisAgentTest extends TestKitSupport {

  private final TestModelProvider analysisModel = new TestModelProvider();

  @Override
  protected TestKit.Settings testKitSettings() {
    return TestKit.Settings.DEFAULT
        .withAdditionalConfig("akka.javasdk.agent.openai.api-key = n/a")
        .withModelProvider(DocumentAnalysisAgent.class, analysisModel);
  }

  @Test
  void agentSendsMultimodalUserMessage() {
    analysisModel
        .whenUserMessage(message -> !message.isTextOnly() && message.contents().size() == 2)
        .reply("The image shows a receipt with line items and totals.");

    var answer =
        componentClient
            .forAgent()
            .inSession("document-analysis-session")
            .method(DocumentAnalysisAgent::analyze)
            .invoke(
                new DocumentAnalysisAgent.AnalyzeRequest(
                    "Compare the image to the PDF.",
                    "https://assets.example.local/receipts/receipt.png",
                    "",
                    ""));

    assertTrue(answer.contains("receipt"));
    assertTrue(answer.contains("totals") || answer.contains("line items"));
  }

  @Test
  void exampleContentLoaderReturnsBytesForImageAndPdf() {
    var loader = new DocumentAnalysisAgent.ExampleContentLoader("token-xyz");

    var imageContent =
        loader.load(
            MessageContent.ImageMessageContent.fromUrl(
                "https://assets.example.local/receipts/photo.png"));
    var pdfContent =
        loader.load(
            MessageContent.PdfMessageContent.fromUrl(
                "https://assets.example.local/receipts/summary.pdf"));

    assertFalse(imageContent.mimeType().isEmpty());
    assertEquals("image/png", imageContent.mimeType().orElseThrow());
    assertEquals("application/pdf", pdfContent.mimeType().orElseThrow());
    assertTrue(new String(imageContent.data(), StandardCharsets.UTF_8).contains("token-xyz"));
    assertTrue(new String(pdfContent.data(), StandardCharsets.UTF_8).contains("summary.pdf"));
  }
}
