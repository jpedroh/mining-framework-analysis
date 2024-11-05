package com.jcabi.github.mock;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Gist;
import com.jcabi.github.Gists;
import com.jcabi.github.Github;
import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.xembly.Directives;

@Immutable @Loggable(value = Loggable.DEBUG) @ToString @EqualsAndHashCode(of = { "storage", "self" }) final class MkGists implements Gists {
  private final transient MkStorage storage;

  private final transient String self;

  MkGists(final MkStorage stg, final String login) throws IOException {
    this.storage = stg;
    this.self = login;
    this.storage.apply(new Directives().xpath("/github").addIf("gists"));
  }

  @Override public Github github() {
    return new MkGithub(this.storage, this.self);
  }

  @Override public Gist create(final Map<String, String> files) throws IOException {
    this.storage.lock();
    final String number;
    try {
      number = Integer.toString(1 + this.storage.xml().xpath(String.format("%s/gist/id/text()", this.xpath())).size());
      final Directives dirs = new Directives().xpath(this.xpath()).add("gist").add("id").set(number).up().add("files");
      for (final Map.Entry<String, String> file : files.entrySet()) {
        dirs.add("file").add("filename").set(file.getKey()).up().add("raw_content").set(file.getValue()).up().up();
      }
      this.storage.apply(dirs);
    }  finally {
      this.storage.unlock();
    }
    return this.get(number);
  }

  @Override public Gist get(final String name) {
    return new MkGist(this.storage, this.self, name);
  }

  @Override public Iterable<Gist> iterate() {
    return new MkIterable<Gist>(this.storage, String.format("%s/gist", this.xpath()), new MkIterable.Mapping<Gist>() {
      @Override public Gist map(final XML xml) {
        return MkGists.this.get(xml.xpath("id/text()").get(0));
      }
    });
  }

  private String xpath() {
    return "/github/gists";
  }

  @Override public void remove(final String name) throws IOException {
    throw new UnsupportedOperationException("This operation is not implemented yet.");
  }
}