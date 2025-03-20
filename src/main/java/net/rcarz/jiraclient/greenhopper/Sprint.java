package net.rcarz.jiraclient.greenhopper;
import lombok.Getter;
import net.rcarz.jiraclient.Field;
import net.rcarz.jiraclient.RestClient;
import net.sf.json.JSONObject;
import java.util.ArrayList;
import org.joda.time.DateTime;
import java.util.List;
import java.util.Map;

/**
 * Represents a GreenHopper sprint.
 */
@Getter public class Sprint extends GreenHopperResource {
  private String name = null;

  private boolean closed = false;

  private DateTime startDate = null;

  private DateTime endDate = null;

  private DateTime completeDate = null;

  private List<Integer> issuesIds = null;

  private List<SprintIssue> issues = null;

  /**
     * Creates a sprint from a JSON payload.
     *
     * @param restclient REST client instance
     * @param json JSON payload
     */
  protected Sprint(RestClient restclient, JSONObject json) {
    super(restclient);
    if (json != null) {
      deserialise(json);
    }
  }

  private void deserialise(JSONObject json) {
    Map map = json;
    id = Field.getInteger(map.get("id"));
    name = Field.getString(map.get("name"));
    closed = map.containsValue("CLOSED");
    startDate = GreenHopperField.getDateTime(map.get("startDate"));
    endDate = GreenHopperField.getDateTime(map.get("endDate"));
    completeDate = GreenHopperField.getDateTime(map.get("completeDate"));
    issuesIds = GreenHopperField.getIntegerArray(map.get("issuesIds"));
  }

  @Override public String toString() {
    return name;
  }

  public List<SprintIssue> getIssues() {
    if (issues == null) {
      issues = new ArrayList<SprintIssue>();
    }
    return issues;
  }
}