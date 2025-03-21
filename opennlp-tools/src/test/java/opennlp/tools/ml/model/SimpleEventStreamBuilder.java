package opennlp.tools.ml.model;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import opennlp.tools.util.ObjectStream;

public class SimpleEventStreamBuilder {
  private final List<Event> eventList = new ArrayList<>();

  private int pos = 0;

  public SimpleEventStreamBuilder add(String event) {
    String[] ss = event.split("/");
    if (ss.length != 2) {
      throw new RuntimeException(String.format("format error of the event \"%s\"", event));
    }
    String[] cvPairs = ss[1].split("\\s+");
    if (cvPairs[0].contains(";")) {
      String[] context = new String[cvPairs.length];
      float[] values = new float[cvPairs.length];
      for (int i = 0; i < cvPairs.length; i++) {
        String[] pair = cvPairs[i].split(";");
        if (pair.length != 2) {
          throw new RuntimeException(String.format("format error of the event \"%s\". " + "\"%s\" doesn\'t have value", event, Arrays.toString(pair)));
        }
        context[i] = pair[0];
        values[i] = Float.parseFloat(pair[1]);
      }
      eventList.add(new Event(ss[0], context, values));
    } else {
      eventList.add(new Event(ss[0], cvPairs));
    }
    return this;
  }

  public ObjectStream<Event> build() {
    return new ObjectStream<Event>() {
      @Override public Event read() throws IOException {
        if (eventList.size() <= pos) {
          return null;
        }
        return eventList.get(pos++);
      }
    };
  }
}