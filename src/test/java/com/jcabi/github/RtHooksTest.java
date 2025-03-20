package com.jcabi.github;
import com.jcabi.aspects.Immutable;
import com.rexsl.test.Request;
import com.rexsl.test.mock.MkAnswer;
import com.rexsl.test.mock.MkContainer;
import com.rexsl.test.mock.MkGrizzlyContainer;
import com.rexsl.test.mock.MkQuery;
import com.rexsl.test.request.JdkRequest;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtHooks}.
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 * @since 0.8
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@Immutable public final class RtHooksTest {
  /**
     * RtHooks can fetch empty list of hooks.
     * @throws Exception if some problem inside
     */
  @Test public void canFetchEmptyListOfHooks() throws Exception {
    final MkContainer container = new MkGrizzlyContainer().next(new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "[]")).start();
    final Hooks hooks = new RtHooks(new JdkRequest(container.home()), RtHooksTest.repo());
    try {
      MatcherAssert.assertThat(hooks.iterate(), Matchers.emptyIterable());
    }  finally {
      container.stop();
    }
  }

  /**
     * RtHooks can fetch non empty list of hooks.
     *
     * @todo #122 RtHooks should iterate multiple hooks. Let's implement
     *  a test here and a method of RtHooks. The method should iterate
     *  multiple hooks. See how it's done in other classes with GhPagination.
     *  When done, remove this puzzle and Ignore annotation from the method.
     */
  @Test @Ignore public void canFetchNonEmptyListOfHooks() {
  }

  /**
     * RtHooks can fetch single hook.
     * @throws Exception if some problem inside
     */
  @Test public void canFetchSingleHook() throws Exception {
    final String name = "hook name";
    final MkContainer container = new MkGrizzlyContainer().next(new MkAnswer.Simple(HttpURLConnection.HTTP_OK, RtHooksTest.hook(name, Collections.<String, String>emptyMap()).toString())).start();
    final Hooks hooks = new RtHooks(new JdkRequest(container.home()), RtHooksTest.repo());
    final Hook hook = hooks.get(1);
    MatcherAssert.assertThat(new Hook.Smart(hook).name(), Matchers.equalTo(name));
    container.stop();
  }

  /**
     * RtHooks can create a hook.
     *
     * @todo #122 RtHooks should be able to create a Hook. Let's implement
     *  a test here and a method create() of RtHooks. The method should create
     *  a hook on some event for some service.
     *  See how it's done in other classes, using Rexsl request/response.
     *  When done, remove this puzzle and Ignore annotation from the method.
     */
  @Test public void canCreateHook() throws Exception {
    final String name = "hook name";
    final ConcurrentHashMap<String, String> config = new ConcurrentHashMap<String, String>(2);
    config.put("url", "http://example.com");
    config.put("content_type", "json");
    final String body = RtHooksTest.hook(name, config).toString();
    final MkContainer container = new MkGrizzlyContainer().next(new MkAnswer.Simple(HttpURLConnection.HTTP_CREATED, body)).next(new MkAnswer.Simple(HttpURLConnection.HTTP_OK, body)).start();
    final Hooks hooks = new RtHooks(new JdkRequest(container.home()), RtHooksTest.repo());
    try {
      final Hook hook = hooks.create(name, config);
      MatcherAssert.assertThat(container.take().method(), Matchers.equalTo(Request.POST));
      MatcherAssert.assertThat(new Hook.Smart(hook).name(), Matchers.equalTo(name));
    }  finally {
      container.stop();
    }
  }

  /**
     * RtHooks can delete a hook.
     *
     * @throws Exception if something goes wrong.
     */
  @Test public void canDeleteHook() throws Exception {
    final MkContainer container = new MkGrizzlyContainer().next(new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT, "")).start();
    final Hooks hooks = new RtHooks(new JdkRequest(container.home()), RtHooksTest.repo());
    hooks.remove(1);
    try {
      final MkQuery query = container.take();
      MatcherAssert.assertThat(query.method(), Matchers.equalTo(Request.DELETE));
      MatcherAssert.assertThat(query.body(), Matchers.isEmptyString());
    }  finally {
      container.stop();
    }
  }

  /**
     * Create and return JsonObject to test.
     * @param name Name of the hook
     * @return JsonObject
     * @throws Exception If some problem inside
     */
  private static JsonObject hook(final String name, final Map<String, String> config) throws Exception {
    final JsonObjectBuilder builder = Json.createObjectBuilder();
    for (final Map.Entry<String, String> entry : config.entrySet()) {
      builder.add(entry.getKey(), entry.getValue());
    }
    return Json.createObjectBuilder().
<<<<<<< /usr/src/app/output/jcabi/jcabi-github/26cda5a366cc3d44e4fc4265b18c085d7cf6de2e/src/test/java/com/jcabi/github/RtHooksTest.java/left.java
    add("name", name).build()
=======
    add("id", 1).add("name", name).add("config", builder).build()
>>>>>>> /usr/src/app/output/jcabi/jcabi-github/26cda5a366cc3d44e4fc4265b18c085d7cf6de2e/src/test/java/com/jcabi/github/RtHooksTest.java/right.java
    ;
  }

  /**
     * Create and return repo for testing.
     * @return Repo
     */
  private static Repo repo() {
    final Repo repo = Mockito.mock(Repo.class);
    Mockito.doReturn(new Coordinates.Simple("test", "hooks")).when(repo).coordinates();
    return repo;
  }
}