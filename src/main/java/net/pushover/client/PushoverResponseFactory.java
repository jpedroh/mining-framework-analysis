package net.pushover.client;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * encapsulate service response parsing / building
 */
public class PushoverResponseFactory {
  private static final Gson GSON = new Gson();


<<<<<<< Unknown file: This is a bug in JDime.
=======
  public static final String REQUEST_REMAINING_HEADER = "X-Limit-App-Remaining";
>>>>>>> /usr/src/app/output/sps/pushover4j/88cd636fabef766f358a3a81d802a07e940a5ceb/src/main/java/net/pushover/client/PushoverResponseFactory.java/right.java


  public static Status createStatus(HttpResponse response) throws IOException {
    if (response == null || response.getEntity() == null) {
      throw new IOException("unreadable response!");
    }
    final String body = EntityUtils.toString(response.getEntity());
    final Status toReturn;
    try {
      toReturn = GSON.fromJson(body, Status.class);
    } catch (JsonSyntaxException e) {
      throw new IOException(e.getCause());
    }

<<<<<<< /usr/src/app/output/sps/pushover4j/88cd636fabef766f358a3a81d802a07e940a5ceb/src/main/java/net/pushover/client/PushoverResponseFactory.java/left.java
    if (m.request != null) {
      toReturn.setRequestId(m.request);
    }
=======
>>>>>>> Unknown file: This is a bug in JDime.

    return toReturn;
  }

  public static Response createResponse(HttpResponse response) throws IOException {
    if (response == null || response.getEntity() == null) {
      throw new IOException("unreadable response!");
    }
    final String body = EntityUtils.toString(response.getEntity());
    final Response toReturn;
    try {
      toReturn = GSON.fromJson(body, Response.class);
    } catch (JsonSyntaxException e) {
      throw new IOException(e.getCause());
    }
    final Header responseId = response.getFirstHeader(REQUEST_REMAINING_HEADER);
    if (responseId != null) {
      try {
        toReturn.setRemaining(Integer.parseInt(responseId.getValue()));
      } catch (Exception ex) {
        toReturn.setRemaining(Integer.MIN_VALUE);
      }
    }
    return toReturn;
  }

  public static Set<PushOverSound> createSoundSet(HttpResponse response) throws IOException {
    if (response == null || response.getEntity() == null) {
      throw new IOException("unreadable response!");
    }
    final String body = EntityUtils.toString(response.getEntity());
    SoundResponse r;
    try {
      r = GSON.fromJson(body, SoundResponse.class);
    } catch (JsonSyntaxException e) {
      throw new IOException(e.getCause());
    }
    final Set<PushOverSound> sounds = new HashSet<PushOverSound>();
    if (r.sounds != null) {
      for (Map.Entry<String, String> e : r.sounds.entrySet()) {
        sounds.add(new PushOverSound(e.getKey(), e.getValue()));
      }
    }
    return sounds;
  }

  public static Verification createVerification(HttpResponse response) throws IOException {
    if (response == null || response.getEntity() == null) {
      throw new IOException("unreadable response!");
    }
    final String body = EntityUtils.toString(response.getEntity());
    Verification v;
    try {
      v = GSON.fromJson(body, Verification.class);
    } catch (JsonSyntaxException e) {
      throw new IOException(e.getCause());
    }
    return v;
  }


<<<<<<< /usr/src/app/output/sps/pushover4j/88cd636fabef766f358a3a81d802a07e940a5ceb/src/main/java/net/pushover/client/PushoverResponseFactory.java/left.java
  private static class ResponseModel {
    int status;

    String request;

    String user;

    List<String> errors = new ArrayList<String>();

    List<String> devices = new ArrayList<String>();

    String receipt;

    int remaining_messages;
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.


  private static class SoundResponse {
    Map<String, String> sounds;
  }
}