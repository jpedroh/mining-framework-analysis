package com.jcabi.github;
import com.jcabi.aspects.Immutable;
import java.io.IOException;
import java.util.Map;
import javax.validation.constraints.NotNull;

/**
 * Github gists.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 * @see <a href="http://developer.github.com/v3/gists/">Gists API</a>
 * @todo #1:1hr New method remove() to delete a gist. Let's add a new
 *  method to remove a gist by name, as explained in
 *  http://developer.github.com/v3/gists/#delete-a-gist. The method
 *  should be tested by unit and integration tests.
 */
@Immutable public interface Gists {
  /**
     * Github we're in.
     * @return Github
     */
  @NotNull(message = "Github is never NULL") Github github();

  /**
     * Create a new gist.
     *
     * @param files Names and content of files
     * @return Gist
     * @throws IOException If there is any I/O problem
     * @see <a href="http://developer.github.com/v3/gists/#create-a-gist">Create a Gist</a>
     */
  @NotNull(message = "gist is never NULL") Gist create(@NotNull(message = "list of files can\'t be NULL") Map<String, String> files) throws IOException;

  /**
     * Get gist by name.
     * @param name Name of it
     * @return Gist
     * @see <a href="http://developer.github.com/v3/gists/#get-a-single-gist">Get a Single Gist</a>
     */
  @NotNull(message = "gist is never NULL") Gist get(@NotNull(message = "name can\'t be NULL") String name);

  /**
     * Iterate all gists.
     * @return Iterator of gists
     * @see <a href="http://developer.github.com/v3/gists/#list-gists">List Gists</a>
     */
  @NotNull(message = "iterable is never NULL") Iterable<Gist> iterate();

  /**
     * Removes a gist by name.
     * @param name Name of the gist to be removed.
     * @throws IOException If there is any I/O problem
     */
  void remove(@NotNull(message = "name is never NULL") String name) throws IOException;
}