package com.jcabi.github;
import com.jcabi.aspects.Immutable;
import java.io.IOException;
import java.util.Map;
import javax.validation.constraints.NotNull;

@Immutable public interface Gists {
  @NotNull(message = "Github is never NULL") Github github();

  @NotNull(message = "gist is never NULL") Gist create(@NotNull(message = "list of files can\'t be NULL") Map<String, String> files) throws IOException;

  @NotNull(message = "gist is never NULL") Gist get(@NotNull(message = "name can\'t be NULL") String name);

  @NotNull(message = "iterable is never NULL") Iterable<Gist> iterate();

  void remove(@NotNull(message = "name is never NULL") String name) throws IOException;
}