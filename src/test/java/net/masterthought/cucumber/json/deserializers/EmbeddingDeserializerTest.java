package net.masterthought.cucumber.json.deserializers;
import com.fasterxml.jackson.databind.JsonNode;
import static org.junit.Assert.assertEquals;
import net.masterthought.cucumber.Configuration;
import static org.powermock.api.mockito.PowerMockito.mock;
import net.masterthought.cucumber.ReportBuilder;
import static org.powermock.api.mockito.PowerMockito.when;
import net.masterthought.cucumber.json.Embedding;
import java.io.File;
import org.apache.commons.io.FileUtils;
import java.io.IOException;
import org.junit.After;
import java.nio.charset.StandardCharsets;
import org.junit.Assert;
import java.util.Base64;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(value = PowerMockRunner.class) @PrepareForTest(value = JsonNode.class) public class EmbeddingDeserializerTest {
  private static final String RANDOM_DIR = "target" + File.separator + System.currentTimeMillis() + File.separator;

  private Configuration configuration;

  @Before public void setUp() {
    configuration = new Configuration(new File(RANDOM_DIR), "TestProject");
    final String directoryPath = RANDOM_DIR + ReportBuilder.BASE_DIRECTORY + configuration.getDirectorySuffixWithSeparator() + "/embeddings";
    final File dir = new File(directoryPath);
    if (!dir.exists()) {
      final boolean created = dir.mkdirs();
      if (!created) {
        Assert.fail("Could not create folder " + directoryPath);
      }
    }
  }

  @After public void cleanUp() throws IOException {
    FileUtils.deleteDirectory(new File(RANDOM_DIR));
  }

  @Test public void deserialize_OnEncodedData_returnsEmbeddingWithEncodedData() {
    EmbeddingDeserializer embeddingDeserializer = new EmbeddingDeserializer();
    JsonNode node = mock(JsonNode.class);
    String data = "This String will be Base64 encoded";
    String encodedData = new String(Base64.getEncoder().encode(data.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
    when(node.get("data")).thenReturn(node);
    when(node.get("mime_type")).thenReturn(node);
    when(node.asText()).thenReturn(encodedData).thenReturn("text/plain");
    Embedding embedding = embeddingDeserializer.deserialize(node, configuration);
    assertEquals("The Decoded Data should be the same as the input Data", data, embedding.getDecodedData());
  }

  @Test public void deserialize_OnUnEncodedData_returnsEmbeddingWithEncodedData() {
    EmbeddingDeserializer embeddingDeserializer = new EmbeddingDeserializer();
    JsonNode node = mock(JsonNode.class);
    String data = "This String will NOT be Base64 encoded";
    when(node.get("data")).thenReturn(node);
    when(node.get("mime_type")).thenReturn(node);
    when(node.asText()).thenReturn(data).thenReturn("text/plain");
    Embedding embedding = embeddingDeserializer.deserialize(node, configuration);
    assertEquals("The Decoded Data should be the same as the input Data", data, embedding.getDecodedData());
  }

  @Test public void deserialize_OnUnEncodedDataWithOnlyValidCharsAndWhiteSpaces_returnsEmbeddingWithEncodedData() {
    EmbeddingDeserializer embeddingDeserializer = new EmbeddingDeserializer();
    JsonNode node = mock(JsonNode.class);
    String data = "This is an normal String";
    when(node.get("data")).thenReturn(node);
    when(node.get("mime_type")).thenReturn(node);
    when(node.asText()).thenReturn(data).thenReturn("text/plain");
    Embedding embedding = embeddingDeserializer.deserialize(node, configuration);
    assertEquals("The Decoded Data should be the same as the input Data", data, embedding.getDecodedData());
  }
}