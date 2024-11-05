package com.jcabi.github.mock;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Hook;
import com.jcabi.github.Hooks;
import com.jcabi.github.Repo;
import java.io.IOException;
import java.util.Collections;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.xembly.Directives;
import java.util.Map;

@Immutable @Loggable(value = Loggable.DEBUG) @ToString @EqualsAndHashCode(of = { "storage", "self", "coords" }) public final class MkHooks implements Hooks {
  private final transient MkStorage storage;

  private final transient String self;

  private final transient Coordinates coords;

  public MkHooks(final MkStorage stg, final String login, final Coordinates rep) throws IOException {
    this.storage = stg;
    this.self = login;
    this.coords = rep;
    this.storage.apply(new Directives().xpath(String.format("/github/repos/repo[@coords=\'%s\']", this.coords)).addIf("hooks"));
  }

  @Override public Repo repo() {
    return new MkRepo(this.storage, this.self, this.coords);
  }

  @Override public Iterable<Hook> iterate() {
    return Collections.emptyList();
  }

  @Override public Hook get(final int number) {
    return new MkHook(this.storage, this.self, this.coords, number);
  }

  @Override public void remove(final int number) throws IOException {
    throw new UnsupportedOperationException("Remove not yet implemented.");
  }

  @Override public Hook create(final String name, final Map<String, String> config) {
    throw new UnsupportedOperationException("Create not yet implemented.");
  }
}