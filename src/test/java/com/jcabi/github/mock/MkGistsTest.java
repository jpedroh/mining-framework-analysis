package com.jcabi.github.mock;
import com.jcabi.github.Gist;
import com.jcabi.github.Gists;
import java.io.IOException;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;

public final class MkGistsTest {
  @Test public void worksWithMockedGists() throws Exception {
    final Gist gist = new MkGithub().gists().create(Collections.singletonMap("test-file-name.txt", "none"));
    final String file = "t.txt";
    gist.write(file, "hello, everybody!");
    MatcherAssert.assertThat(gist.read(file), Matchers.startsWith("hello, "));
  }

  @Test @Ignore public void removesMkGistByName() throws Exception {
    final Gists gists = new MkGithub().gists();
    final Gist createdGist = gists.create(Collections.singletonMap("fileName.txt", "content"));
    MatcherAssert.assertThat(gists.iterate(), Matchers.hasItem(createdGist));
    gists.remove("gist");
    MatcherAssert.assertThat(gists.iterate(), Matchers.not(Matchers.hasItem(createdGist)));
  }

  @Test public void worksWithSeveralGists() throws Exception {
    final Gists gists = new MkGithub().gists();
    final Gist gist = gists.create(Collections.singletonMap("test-file-name.txt", "none"));
    final Gist othergist = gists.create(Collections.singletonMap("test-file-name2.txt", ""));
    final String file = "t.txt";
    gist.write(file, "hello, everybody!");
    othergist.write(file, "bye, everybody!");
    MatcherAssert.assertThat(gist.read(file), Matchers.startsWith("hello, "));
    MatcherAssert.assertThat(othergist.read(file), Matchers.startsWith("bye, "));
  }

  @Test public void testStar() throws Exception {
    final Gist gist = new MkGithub().gists().create(Collections.singletonMap("file-name.txt", ""));
    MatcherAssert.assertThat(gist.starred(), Matchers.equalTo(false));
    gist.star();
    MatcherAssert.assertThat(gist.starred(), Matchers.equalTo(true));
  }

  @Test public void createGistWithEmptyFile() throws IOException {
    final String filename = "file.txt";
    final Gist gist = new MkGithub().gists().create(Collections.singletonMap(filename, ""));
    MatcherAssert.assertThat(gist.read(filename), Matchers.isEmptyString());
  }
}