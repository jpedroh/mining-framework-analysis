package opennlp.tools.ml;
import java.io.IOException;
import java.util.Map;
import opennlp.tools.ml.model.DataIndexer;
import opennlp.tools.ml.model.Event;
import opennlp.tools.ml.model.MaxentModel;
import opennlp.tools.util.ObjectStream;

public interface EventTrainer {
  public static final String EVENT_VALUE = "Event";

  public void init(Map<String, String> trainParams, Map<String, String> reportMap);

  public MaxentModel train(ObjectStream<Event> events) throws IOException;

  public MaxentModel train(DataIndexer indexer) throws IOException;
}