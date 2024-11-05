package com.jcabi.github;
import com.jcabi.aspects.Immutable;
import java.io.IOException;
import javax.validation.constraints.NotNull;
import java.util.Map;

@Immutable public interface Hooks {
  @NotNull(message = "repository is never NULL") Repo repo();

  @NotNull(message = "iterable is never NULL") Iterable<Hook> iterate();

  void remove(int number) throws IOException;

  @NotNull(message = "hook is never NULL") Hook get(int number);

  @NotNull(message = "hook is never NULL") Hook create(String name, Map<String, String> config) throws IOException;
}