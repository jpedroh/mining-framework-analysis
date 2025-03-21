package net.masterthought.cucumber.json;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.commons.lang.ArrayUtils;
import net.masterthought.cucumber.json.deserializers.OutputsDeserializer;
import net.masterthought.cucumber.json.support.Argument;
import net.masterthought.cucumber.json.support.Resultsable;

public class Step implements Resultsable {
  private String name = null;

  private final String keyword = null;

  private final Result result = new Result();

  private final Row[] rows = new Row[0];


<<<<<<< Unknown file: This is a bug in JDime.
=======
  @JsonProperty(value = "arguments") private final Argument[] arguments = new Argument[0];
>>>>>>> /usr/src/app/output/masterthought/cucumber-reporting/75f67d3da0f458007b14998d6e8aa96ef4e40104/src/main/java/net/masterthought/cucumber/json/Step.java/right.java


  private final Match match = null;

  private final Embedding[] embeddings = new Embedding[0];

  @JsonDeserialize(using = OutputsDeserializer.class) @JsonProperty(value = "output") private final Output[] outputs = new Output[0];

  @JsonProperty(value = "doc_string") private final DocString docString = null;

  public Row[] getRows() {
    if (ArrayUtils.getLength(arguments) == 1) {
      return arguments[0].getRows();
    } else {
      if (ArrayUtils.getLength(arguments) > 1) {
        throw new UnsupportedOperationException("\'arguments\' length should be equal to 1");
      } else {
        return rows;
      }
    }
  }

  public String getName() {
    return name;
  }

  public String getKeyword() {
    return keyword.trim();
  }

  @Override public Output[] getOutputs() {
    return outputs;
  }

  @Override public Match getMatch() {
    return match;
  }

  public Embedding[] getEmbeddings() {
    return embeddings;
  }

  @Override public Result getResult() {
    return result;
  }

  public long getDuration() {
    return result.getDuration();
  }

  public DocString getDocString() {
    return docString;
  }
}