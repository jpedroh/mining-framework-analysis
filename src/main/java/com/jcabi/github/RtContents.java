package com.jcabi.github;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.rexsl.test.Request;
import com.rexsl.test.response.JsonResponse;
import com.rexsl.test.response.RestResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import javax.json.Json;
import javax.json.JsonStructure;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;

/**
 * Github contents.
 *
 * @author Andres Candal (andres.candal@rollasolution.com)
 * @version $Id$
 * @since 0.8
 */
@Immutable @Loggable(value = Loggable.DEBUG) @EqualsAndHashCode(of = { "entry", "request" }) public final class RtContents implements Contents {
  /**
     * API entry point.
     */
  private final transient Request entry;

  /**
     * Repository.
     */
  private final transient Repo owner;

  /**
     * RESTful request.
     */
  private final transient Request request;

  /**
     * Public ctor.
     * @param req RESTful API entry point
     * @param repo Repository
     */
  public RtContents(final Request req, final Repo repo) {
    this.entry = req;
    this.owner = repo;
    final Coordinates coords = repo.coordinates();
    this.request = req.uri().path("/repos").path(
<<<<<<< /usr/src/app/output/jcabi/jcabi-github/25f907f6579e89ddd5f7aaccd2507a89a56b94c2/src/main/java/com/jcabi/github/RtContents.java/left.java
    repo.coordinates().user()
=======
    coords.user()
>>>>>>> /usr/src/app/output/jcabi/jcabi-github/25f907f6579e89ddd5f7aaccd2507a89a56b94c2/src/main/java/com/jcabi/github/RtContents.java/right.java
    ).
<<<<<<< /usr/src/app/output/jcabi/jcabi-github/25f907f6579e89ddd5f7aaccd2507a89a56b94c2/src/main/java/com/jcabi/github/RtContents.java/left.java
    path(repo.coordinates().repo()).path("/contents").back()
=======
    path(coords.repo()).back()
>>>>>>> /usr/src/app/output/jcabi/jcabi-github/25f907f6579e89ddd5f7aaccd2507a89a56b94c2/src/main/java/com/jcabi/github/RtContents.java/right.java
    ;
  }

  @Override public Repo repo() {
    return this.owner;
  }

  @Override public Content readme() {
    return new RtReadme(this.entry, this.
<<<<<<< /usr/src/app/output/jcabi/jcabi-github/25f907f6579e89ddd5f7aaccd2507a89a56b94c2/src/main/java/com/jcabi/github/RtContents.java/left.java
    owner
=======
    request
>>>>>>> /usr/src/app/output/jcabi/jcabi-github/25f907f6579e89ddd5f7aaccd2507a89a56b94c2/src/main/java/com/jcabi/github/RtContents.java/right.java
    );
  }

  @Override public Commit remove(@NotNull(message = "path is never NULL") final String path, @NotNull(message = "message is never NULL") final String message, @NotNull(message = "sha is never NULL") final String sha) throws IOException {
    final JsonStructure json = Json.createObjectBuilder().add("message", message).add("sha", sha).build();
    return new RtCommit(this.entry, this.owner, this.request.method(Request.DELETE).uri().path(path).back().body().set(json).back().fetch().as(RestResponse.class).assertStatus(HttpURLConnection.HTTP_OK).as(JsonResponse.class).json().readObject().getJsonObject("commit").getString("sha"));
  }
}