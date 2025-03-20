package com.jcabi.github;
import com.jcabi.aspects.Immutable;
import java.io.IOException;
import java.util.Map;
import javax.validation.constraints.NotNull;

/**
 * Github hooks.
 *
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 * @since 0.8
 * @see <a href="http://developer.github.com/v3/repos/hooks/">Hooks API</a>
 */
@Immutable public interface Hooks {
  /**
     * Owner of them.
     * @return Repo
     */
  @NotNull(message = "repository is never NULL") Repo repo();

  /**
     * Iterate them all.
     * @return Iterator of hooks
     * @see <a href="http://developer.github.com/v3/repos/hooks/#list">List</a>
     */
  @NotNull(message = "iterable is never NULL") Iterable<Hook> iterate();

  /**
     * Remove hook by ID.
     * @param number ID of the label to remove
     * @throws IOException If there is any I/O problem
     * @see <a href="http://developer.github.com/v3/repos/hooks/#delete-a-hook">List</a>
     */
  void remove(int number) throws IOException;

  /**
     * Get specific hook by number.
     * @param number Hook number
     * @return Hook
     * @see <a href="http://developer.github.com/v3/repos/hooks/#get-single-hook">Get single hook</a>
     */
  @NotNull(message = "hook is never NULL") Hook get(int number);

  /**
     * Create new hook.
     * @param name Hook name
     * @param config Configuration for the hook
     * @return Hook
     * @throws IOException If there is any I/O problem
     * @see <a href="http://developer.github.com/v3/repos/hooks/#create-a-hook">Create a hook</a>
     */
  @NotNull(message = "hook is never NULL") Hook create(String name, Map<String, String> config) throws IOException;
}